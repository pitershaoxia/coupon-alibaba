package com.alibaba.coupon.module.service;

import com.alibaba.coupon.domain.entity.CouponTemplate;

public interface MyAsyncService {

    void asyncBuildCouponByTemplate(CouponTemplate template);
}
