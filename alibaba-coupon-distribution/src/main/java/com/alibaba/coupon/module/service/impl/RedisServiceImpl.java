package com.alibaba.coupon.module.service.impl;

import com.alibaba.coupon.domain.entity.Coupon;
import com.alibaba.coupon.domain.enums.CouponStatusEnums;
import com.alibaba.coupon.module.service.RedisService;
import com.alibaba.fastjson.JSON;
import com.simple.coupon.common.constant.CacheConstant;
import com.simple.coupon.common.exception.MyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: 缓存服务
 * @Author: LiuPing
 * @Time: 2020/9/23 0023 -- 15:58
 */
@Slf4j
@Service
@SuppressWarnings("all")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 根据userID 和 状态查询缓存的优惠券列表
     *
     * @param userId
     * @param status
     * @return
     */
    @Override
    public List<Coupon> getCacheCoupons(Long userId, Integer status) {
        log.debug("准备从cache中获取coupon: userId = {}, status = {}", userId, status);
        String redisKey = status2RedisKey(status, userId);

        // 从redis取出值来
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(couponStrs)) {
            log.debug("【准备保存无效优惠券cache信息给当前用户】");
            // 保存空的优惠券列表到缓存中
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }

        return couponStrs.stream()
                .map(cs -> JSON.parseObject(cs, Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * 从cache中获取一个优惠券码
     *
     * @param templateId 优惠券模板主键
     * @return
     */
    @Override
    public String tryToAcquireCouponCodeFromRedis(Integer templateId) {
        String redisKey = String.format("%s%s", CacheConstant.RedisPrefix_COUPON_TEMPLATE, templateId.toString());
        // 因为优惠券码不存在顺序关系，左边pop或右边pop，没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);

        log.info("Acquire Coupon Code: {},{},{}", templateId, redisKey, couponCode);

        return couponCode;
    }

    /**
     * 将优惠券保存到cache中
     * 这部分的服务实际用途的业务思想要了解
     * 用户手中的优惠券数量会影响到缓存中的各个分类的优惠券数量
     *
     * @param userId  用户Id
     * @param coupons
     * @param status  优惠券状态
     * @return
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) {
        log.debug("准备Add coupon to cache, userId = {}, status = {}, coupons = {}", userId, status, JSON.toJSONString(coupons));
        Integer result = -1;
        CouponStatusEnums couponStatus = CouponStatusEnums.getCouponStatusByCode(status);

        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * 将过期的优惠券添加到cache 中
     * status 是 expired 表示已有的优惠券过期了，会影响到两个 cache状态
     * usable 类做减法，expired 类做加法
     *
     * @return
     */
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons) {
        log.debug("Add coupon to cache for expired....");

        HashMap<String, String> needCacheForExpired = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(CouponStatusEnums.USABLE.getCode(), userId);
        String redisKeyForExpired = status2RedisKey(CouponStatusEnums.EXPIRED.getCode(), userId);

        // 获取当前用户手中可用状态的优惠券
        List<Coupon> userCurUsableCoupons = getCacheCoupons(userId, CouponStatusEnums.USABLE.getCode());
        // 当前用户的优惠券个数一定是大于1的 （因为至少会添加一个无效的优惠券个数）
        assert userCurUsableCoupons.size() > coupons.size();
        coupons.forEach(
                c -> {
                    needCacheForExpired.put(c.getId().toString(), JSON.toJSONString(c));
                }
        );
        // 校验当前用户的优惠券参数是否与cache 中匹配
        List<Integer> userCurUsableIds = userCurUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, userCurUsableIds)) {
            // 如果当前传入的coupon不是用户的coupons的子集就会报错
            log.error("当前用户的CurCoupons与缓存中的不匹配,userId = {}, userCurUsableIds= {}, paramIds= {}",
                    userId, JSON.toJSONString(userCurUsableCoupons), JSON.toJSONString(paramIds));
            throw new MyException(400, "当前用户的CurCoupons与缓存中的不匹配....");
        }
        List<String> needCleanKey = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {

                // 1.已过期的优惠券在cache做加法
                redisOperations.opsForHash().putAll(redisKeyForExpired, needCacheForExpired);
                // 2.可用的优惠券在cache中做减法
                redisOperations.opsForHash().delete(redisKeyForUsable, needCleanKey.toArray());
                // 3.重置过期时间
                redisOperations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisKeyForExpired,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("【Pipeline exe result】= {}", redisTemplate.executePipelined(sessionCallback));
        return coupons.size();
    }

    /**
     * 新增已使用的优惠券加入到cache中
     * 如果 status 是used,代表用户操作是当前的优惠券，它会影响到2个cache的状态
     * cache中的usable做减法，used的做加法
     *
     * @return
     */
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) {
        HashMap<String, String> needCacheForUsed = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(CouponStatusEnums.USABLE.getCode(), userId);
        String redisKeyForUsed = status2RedisKey(CouponStatusEnums.USED.getCode(), userId);

        // 获取当前用户可用状态的优惠券
        List<Coupon> userCurUsableCoupons = getCacheCoupons(userId, CouponStatusEnums.USABLE.getCode());
        // 当前用户可用的优惠券个数一定是大于1的 （因为至少会添加一个无效的优惠券个数）
        assert userCurUsableCoupons.size() > coupons.size();
        coupons.forEach(
                c -> {
                    needCacheForUsed.put(c.getId().toString(), JSON.toJSONString(c));
                }
        );
        // 校验当前用户的优惠券参数是否与cache 中匹配
        List<Integer> userCurUsableIds = userCurUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, userCurUsableIds)) {
            // 如果当前传入的coupon不是用户可用的coupons的子集就会报错
            log.error("当前用户的CurCoupons与缓存中的不匹配,userId = {}, userCurUsableIds= {}, paramIds= {}",
                    userId, JSON.toJSONString(userCurUsableCoupons), JSON.toJSONString(paramIds));
            throw new MyException(400, "当前用户的CurCoupons与缓存中的不匹配....");
        }
        List<String> needCleanKey = paramIds.stream()
                .map(p -> p.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                // 1. 已使用的优惠券 在cache做加法
                redisOperations.opsForHash().putAll(redisKeyForUsed, needCacheForUsed);
                // 2. 可用的优惠券 在cache做减法
                redisOperations.opsForHash().delete(redisKeyForUsable, needCleanKey);
                // 3. 重置过期时间
                redisOperations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                redisOperations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline exe result = {}", redisTemplate.executePipelined(sessionCallback));
        return coupons.size();
    }

    /**
     * 新增优惠券到cache中
     * 如果 status 是usable 只会影响到 可用的优惠券类型
     *
     * @return
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {
        log.debug("【add Coupon To cache for Usable...】");
        HashMap<String, String> needCacheCoupon = new HashMap<>();
        coupons.forEach(
                c -> {
                    needCacheCoupon.put(c.getId().toString(), JSON.toJSONString(c));
                    // 获取当前用户status 的 redisKey
                    String redisKey = status2RedisKey(CouponStatusEnums.USABLE.getCode(), userId);
                    redisTemplate.opsForHash().putAll(redisKey, needCacheCoupon);
                    log.debug("Add Usable coupon to cache: userId = {}, num = {}", userId, needCacheCoupon.size());

                    redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                }
        );
        return needCacheCoupon.size();
    }

    /**
     * 保存空的优惠券列表到缓存中 -- 目的：避免缓存穿透
     *
     * @param userId
     * @param singletonList
     */
    public void saveEmptyCouponListToCache(Long userId, List<Integer> singletonList) {
        log.debug("【Save Empty List To Cache, UserId = {}, status = {}", userId, JSON.toJSONString(singletonList));

        HashMap<String, Object> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        // 使用SessinCallback 把数据命令放入到redis 的 pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                singletonList.forEach(
                        s -> {
                            String redisKey = status2RedisKey(s, userId);
                            redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                        }
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result num: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    /**
     * 根据status ＋　userId 获取到对应的redis key
     *
     * @param status
     * @param userId
     * @return
     */
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatusEnums couponStatus = CouponStatusEnums.getCouponStatusByCode(status);

        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s", CacheConstant.RedisPrefix_USER_COUPON_USEABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s", CacheConstant.RedisPrefix_USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", CacheConstant.RedisPrefix_USER_COUPON_EXPIRED, userId);
                break;
        }
        return redisKey;
    }

    /**
     * <h2>获取一个随机的过期时间</h2>
     * 缓存雪崩：key 在同一时间失效
     *
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回[min, max] 之间的随机数
     */
    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(
                min * 60 * 60,
                max * 60 * 60
        );
    }
}
