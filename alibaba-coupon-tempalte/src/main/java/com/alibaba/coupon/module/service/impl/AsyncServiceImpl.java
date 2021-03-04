package com.alibaba.coupon.module.service.impl;

import com.alibaba.coupon.domain.entity.CouponTemplate;
import com.alibaba.coupon.module.dao.CouponTemplateMapper;
import com.alibaba.coupon.module.service.MyAsyncService;
import com.google.common.base.Stopwatch;
import com.simple.coupon.common.constant.CacheConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: <h2>异步服务操作</h2>
 * @Author: LiuPing
 * @Time: 2020/9/17 0017 -- 14:12
 */
@Slf4j
@Service
@SuppressWarnings("all")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncServiceImpl implements MyAsyncService {

    private final CouponTemplateMapper templateMapper;

    /**
     * 注入 Redis 模板类
     */
    private final StringRedisTemplate redisTemplate;

    @Async("getAsyncExecutor")
    @Override
    public void asyncBuildCouponByTemplate(CouponTemplate template) {
        // 执行时间监听器
        Stopwatch stopwatch = Stopwatch.createStarted();

        // 构造优惠券码
        Set<String> couponCodes = buildCouponCode(template);

        String redisKey = String.format("%s%s", CacheConstant.RedisPrefix_COUPON_TEMPLATE, template.getId().toString());
        Long count = redisTemplate.opsForList().rightPushAll(redisKey, couponCodes);    // 写入redis中
        log.debug("【存入redis中的优惠券码数据量count】= {}", count);

        template.setAvailable(true);    // 标识模板可用
        templateMapper.updateAvailable(template);  // 更新最新状态

        stopwatch.stop();
        log.info("【Construct CouponCode By Template Cost】 = {}ms", stopwatch.elapsed(TimeUnit.MICROSECONDS));
        // todo 发送短信或邮件通知优惠券模板可用了
        log.debug("CouponTemplate({}) is available", template.getId());
    }

    /**
     * <h2>构造优惠券码</h2>
     * 规则：
     * 优惠券码（对应于每一种优惠券，18位）
     * 前四位：产品线(1)+优惠券分类(001)
     * 中间六位：日期随机（190805）
     * 后八位：0-9 随机数生成
     *
     * @return
     */
    private Set<String> buildCouponCode(CouponTemplate template) {
        // 执行监听器
        Stopwatch stopwatch = Stopwatch.createStarted();
        // 使用set数据结构避免出现重复数据
        Set<String> codeSet = new HashSet<>(template.getCount());
        String prefix4 = template.getProduct_line().getCode() + template.getCategory().getCode() + "";
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());
        for (Integer i = 0; i < template.getCount(); i++) {
            codeSet.add(prefix4 + buildCounponCodeSuffix14(date));
        }
        stopwatch.stop();
        log.debug("【redis的优惠券码创建耗时】= {}ms", stopwatch.elapsed(TimeUnit.MICROSECONDS));
        return codeSet;
    }

    /**
     * 构造后14位
     *
     * @return
     */
    private String buildCounponCodeSuffix14(String date) {
        char[] bases = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        List<Character> chars = date.chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
        Collections.shuffle(chars); // 将输入的list进行排序打乱并返回

        // 生成日期的随机数6位
        String midfix6 = chars.stream()
                .map(Objects::toString)
                .collect(Collectors.joining());
        // 生成后8位
        String suffix8 = RandomStringUtils.random(1, bases) // 在base范围中随机选一个数
                + RandomStringUtils.randomNumeric(7); // 在0-9中随机生成7个数

        return midfix6 + suffix8;
    }
}
