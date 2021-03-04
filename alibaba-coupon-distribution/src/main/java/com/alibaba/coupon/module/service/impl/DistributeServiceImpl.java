package com.alibaba.coupon.module.service.impl;

import com.alibaba.coupon.domain.dto.AcquireTemplateRequestDTO;
import com.alibaba.coupon.domain.dto.CouponClassifyDTO;
import com.alibaba.coupon.domain.dto.CouponKafkaMessageDTO;
import com.alibaba.coupon.domain.entity.Coupon;
import com.alibaba.coupon.domain.enums.CouponStatusEnums;
import com.alibaba.coupon.module.dao.CouponMapper;
import com.alibaba.coupon.module.feign.SettlementFeignClient;
import com.alibaba.coupon.module.feign.TemplateFeignClient;
import com.alibaba.coupon.module.service.DistributeService;
import com.alibaba.coupon.module.service.RedisService;
import com.alibaba.fastjson.JSON;
import com.simple.coupon.common.constant.CacheConstant;
import com.simple.coupon.common.exception.MyException;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.dto.GoodsInfoDTO;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import com.simple.coupon.domain.vo.RespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/23 0023 -- 15:57
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DistributeServiceImpl implements DistributeService {


    private final CouponMapper couponMapper;

    /* redis 服务 */
    private final RedisService redisService;

    /* 模板微服务客户端 */
    private final TemplateFeignClient templateFeignClient;

    /* 结算微服务客户端 */
    private final SettlementFeignClient settlementFeignClient;

    /* kafka 客户端*/
    private final KafkaTemplate<String, String> kafkaTemplate;


    /**
     * 根据用户id 和 优惠券状态查询用户的优惠券记录
     *
     * @return
     */
    @Override
    public List<Coupon> findCouponByStatus(Long userId, Integer status) {
        // 获取当前用户的redis中的优惠券
        List<Coupon> curCached = redisService.getCacheCoupons(userId, status);
        List<Coupon> preTarget;
        // 缓存中对当前用户的优惠券进行校验
        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("Coupon cache is not empty = {}, {}", userId, status);
            preTarget = curCached;
        } else {
            // 这里存在缓存穿透的问题，但已经解决,（缓存数据失效，请求查询DB）
            log.debug("redis 缓存中的coupon信息为null，准备从DB查询记录 userId = {},status = {}", userId, status);
            List<Coupon> dbCoupons = couponMapper.findAllByUserIdAndStatus(userId, CouponStatusEnums.getCouponStatusByCode(status).getCode());
            // 如果数据库没记录，直接返回就可以，cache中已经加入了一张无效的优惠券（至少有一张）
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("【当前该用户没有优惠券】userId = {}, status = {}", userId, status);
                return dbCoupons;
            }
            // 填充dbCoupons 的 CouponTemplateDTO 字段
            Map<Integer, CouponTemplateDTO> ids2TemplateDTO = templateFeignClient.findIds2TemplateDTO(
                    dbCoupons.stream()
                            .map(Coupon::getTemplateId)
                            .collect(Collectors.toList())
            );
            dbCoupons.forEach(
                    coupon -> coupon.setTemplateSDK(ids2TemplateDTO.get(coupon.getTemplateId()))
            );
            // 数据库中存入记录
            preTarget = dbCoupons;
            // 将记录写入redis cache 中
            redisService.addCouponToCache(userId, preTarget, status);
        }
        // todo 将无效的优惠券剔除
        preTarget = preTarget.stream()
                .filter(c -> c.getId() != -1)
                .collect(Collectors.toList());
        // 如果当前获取的是可用状态优惠券，还需要对已过期优惠券的延迟处理
        if (CouponStatusEnums.getCouponStatusByCode(status) == CouponStatusEnums.USABLE) {
            CouponClassifyDTO classify = CouponClassifyDTO.classify(preTarget);
            // 如果已过期状态不为空，需要做延迟处理
            if (CollectionUtils.isNotEmpty(classify.getExpireList())) {
                log.debug("增加过期优惠券信息到redis中,userId = {}, status = {}", userId, status);

                redisService.addCouponToCache(
                        userId, classify.getExpireList(), CouponStatusEnums.EXPIRED.getCode()
                );
                // 因为操作DB比较耗时，发送到kafka做异步处理
                kafkaTemplate.send(CacheConstant.TOPIC,
                        JSON.toJSONString(
                                new CouponKafkaMessageDTO(
                                        CouponStatusEnums.EXPIRED.getCode(),
                                        classify.getExpireList().stream()
                                                .map(Coupon::getId)
                                                .collect(Collectors.toList())
                                )
                        ));
            }
            return classify.getUsableList();
        }
        return preTarget;
    }

    /**
     * 根据用户id 查询当前可以领取的优惠券模板
     *
     * @return
     */
    @Override
    public ResponseEntity<RespVo<?>> findAvailableTemplate(Long userId) {
        long curTime = new Date().getTime();
        List<CouponTemplateDTO> templateDTOs = templateFeignClient.findAllUsableTemplate().getData();
        log.debug("【找到所有模板从(TemplateFeignClient) 数量为】= {}", templateDTOs.size());

        // 过滤已过期的优惠券模板 -> 筛选未过期的
        templateDTOs = templateDTOs.stream()
                .filter(t -> t.getRule().getExpiration().getDeadline() > curTime)
                .collect(Collectors.toList());
        log.debug("该用户可用的优惠券数量count = {}", templateDTOs.size());
        /*
         * key 是 templateId ,value 使用了动态数据结构 (common lang3下的包)
         * value 中的left 是 template limitation 领取次数, right 是优惠券模板信息
         * */
        Map<Integer, Pair<Integer, CouponTemplateDTO>> limit2Template = new HashMap<>(templateDTOs.size());
        // map k-模板主键
        templateDTOs.forEach(
                t -> limit2Template.put(
                        t.getId(),
                        Pair.of(t.getRule().getLimitation(), t)
                )
        );
        List<CouponTemplateDTO> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUseableCoupons = findCouponByStatus(userId, CouponStatusEnums.USABLE.getCode());
        log.debug("当前该用户拥有可用状态的优惠券数量,userId = {},num = {}", userId, userUseableCoupons.size());
        // key 是templateId
        Map<Integer, List<Coupon>> template2Coupons = userUseableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        // 根据template 的rule 来判断是否可以领取优惠券模板
        limit2Template.forEach(
                (k, v) -> {
                    Integer limitation = v.getLeft();
                    CouponTemplateDTO templateDTO = v.getRight();
                    if (template2Coupons.containsKey(k) && template2Coupons.get(k).size() >= limitation) {
                        // 该用户的优惠券模板领取次数达到上限，不能再领取了
                        return;
                    }
                    result.add(templateDTO);
                }
        );
        return ResponseEntity.ok(new RespVo<>(result));
    }

    /**
     * 用户领取优惠券
     * 1、从 templateClient 拿到对应的优惠券，并检查是否过期
     * 2、根据 limitation 判断用户是否可以领取优惠券
     * 3、save to db
     * 4、填充 couponTemplateDTO 字段
     * 5、save to redis
     *
     * @return
     */
    @Override
    public ResponseEntity<RespVo<?>> acquireTemplate(AcquireTemplateRequestDTO dto) {
        Integer templateId = dto.getTemplateDTO().getId();
        Map<Integer, CouponTemplateDTO> id2Template = templateFeignClient.findIds2TemplateDTO(
                Collections.singletonList(templateId));
        // 1.优惠券模板是否存在
        if (id2Template.size() <= 0) {
            log.error("Can not acquire template from TemplateClient: {}", dto.getTemplateDTO().getId());
            throw new MyException(502, "Can not acquire template from TemplateClient");
        }
        // 2.用户是否可以领取这张优惠券
        List<Coupon> userUsableCoupons = findCouponByStatus(dto.getUserId(), CouponStatusEnums.USABLE.getCode());
        // 判断一下用户是否第一次领取
        if (userUsableCoupons.size() == 0) {
            // 第一次领取
            log.debug("该用户第一次想获取优惠券userId = " + dto.getUserId());
        } else {
            // 用户在coupon表有领取记录了
            // 下方key是coupon的templateId,value是coupon
            Map<Integer, List<Coupon>> template2Coupons = userUsableCoupons.stream()
                    .collect(Collectors.groupingBy(Coupon::getTemplateId));
            int size = template2Coupons.get(templateId).size();
            Integer limitation = dto.getTemplateDTO().getRule().getLimitation();
            if (template2Coupons.containsKey(templateId) && size > limitation) {
                log.error("该用户已领取过了,userId = {}, templateId = {}", dto.getUserId(), templateId);
                throw new MyException(500, "您领取的优惠券数量达到上限了");
            }
        }
        // 3.尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCodeFromRedis(templateId);
        if (StringUtils.isEmpty(couponCode)) {
            log.error("can not acquire coupon code: {}", templateId);
            throw new MyException(500, "优惠券已经分发完毕了，下次早点过来");
        }
        Coupon newCoupon = Coupon.builder()
                .templateId(templateId)
                .userId(dto.getUserId())
                .couponCode(couponCode)
                .assignTime(new Date())
                .status(CouponStatusEnums.USABLE)
                .build();
        couponMapper.insertUserGetCoupon(newCoupon);
        // 填充 newCoupon 中的 couponTemplateDTO 字段，一定要在放入redis之前完成
        newCoupon.setTemplateSDK(dto.getTemplateDTO());
        // 放入redis
        log.debug("插入后的Coupon ＝　{}", JSON.toJSONString(newCoupon));
        redisService.addCouponToCache(
                dto.getUserId(),
                Collections.singletonList(newCoupon),
                CouponStatusEnums.USABLE.getCode()
        );
        return ResponseEntity.ok(new RespVo<>(newCoupon));
    }

    /**
     * 结算（核销）优惠券
     * 这里需要注意，规则相关的处理需要 settlement 服务来实现，当前服务中仅仅做
     * 业务处理过程（校验过程）
     *
     * @return
     */
    @Override
    public SettlementInfoDTO settlement(SettlementInfoDTO infoDTO) {
        log.info("settlement 传入参数内容 = {}", JSON.toJSONString(infoDTO));
        List<SettlementInfoDTO.CouponAndTemplateDTO> ctInfos = infoDTO.getCouponAndTemplateInfos();
        if (CollectionUtils.isEmpty(ctInfos)) {
            log.debug("empty coupons for settle...");
            double goodsSum = 0.0;
            for (GoodsInfoDTO goodsInfo : infoDTO.getGoodsInfos()) {
                goodsSum += goodsInfo.getPrice() * goodsInfo.getCount();
            }
            // 没有优惠券也就不存在优惠券的核销，SettlementInfoDTO 的其他字段不修改
            infoDTO.setCost(retain2Decimals(goodsSum));
        }
        // 校验入参的优惠券是否是用户自己的
        List<Coupon> coupons = findCouponByStatus(infoDTO.getUserId(), CouponStatusEnums.USABLE.getCode());
        // key 是couponId, value 是coupon
        Map<Integer, Coupon> couponMap = coupons.stream()
                .collect(Collectors.toMap(Coupon::getId, Function.identity()));
        List<Integer> list = ctInfos.stream()
                .map(SettlementInfoDTO.CouponAndTemplateDTO::getId)
                .collect(Collectors.toList());
        if (MapUtils.isEmpty(couponMap) || !CollectionUtils.isSubCollection(list, couponMap.keySet())) {
            log.debug("参数1 = {}", couponMap.keySet());
            log.debug("参数2 = {}", list);
            log.error("User coupon has some problems, It is not subcollection of coupons");
            throw new MyException(500, "User coupon has some problems, It is not subcollection of coupons");
        }
        log.debug("Current Settlement Coupons Is User's = {}", ctInfos.size());
        List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
        // 获取需要核销的coupons
        ctInfos.forEach(
                c -> settleCoupons.add(couponMap.get(c.getId()))
        );
        // 通过settlement 服务来获取结算信息
        SettlementInfoDTO processedInfo = settlementFeignClient.computeRule(infoDTO);
        if (processedInfo.getEmploy() && CollectionUtils.isNotEmpty(processedInfo.getCouponAndTemplateInfos())) {
            log.debug("Settle user coupon: userId = {}, settleInfo = {}", infoDTO.getUserId(), JSON.toJSONString(settleCoupons));
            // 更新缓存
            redisService.addCouponToCache(
                    infoDTO.getUserId(),
                    settleCoupons,
                    CouponStatusEnums.USED.getCode()
            );
            // 更新DB
            kafkaTemplate.send(
                    CacheConstant.TOPIC,
                    JSON.toJSONString(
                            new CouponKafkaMessageDTO(
                                    CouponStatusEnums.USED.getCode(),
                                    settleCoupons.stream()
                                            .map(Coupon::getId).collect(Collectors.toList())
                            )
                    )
            );

        }
        return processedInfo;
    }

    /**
     * 保留2位小数
     *
     * @param value
     * @return
     */
    private Double retain2Decimals(double value) {
        return new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
}
