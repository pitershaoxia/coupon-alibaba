package com.alibaba.coupon.module.service;

import com.alibaba.coupon.domain.entity.Coupon;

import java.util.List;

public interface RedisService {

    /**
     * <h2>根据userId 和 状态 找到缓存中优惠券列表数据</h2>
     *
     * @param userId 用户ID
     * @param status 优惠券状态
     * @return 返回null，表示没有改记录
     */
    List<Coupon> getCacheCoupons(Long userId, Integer status);

    /**
     * <h2>保存空的优惠券列表到缓存中</h2>
     *
     * @param userId 用户ID
     * @param status 优惠券状态列表
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * <h2>尝试从cache 中获取一个优惠券码</h2>
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    String tryToAcquireCouponCodeFromRedis(Integer templateId);

    /**
     * <h2>将优惠券保存到cache中</h2>
     *
     * @param userId  用户Id
     * @param coupons
     * @param status  优惠券状态
     * @return 保存成功的个数
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status);
}
