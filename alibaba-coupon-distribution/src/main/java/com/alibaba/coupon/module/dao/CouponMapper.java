package com.alibaba.coupon.module.dao;

import com.alibaba.coupon.domain.entity.Coupon;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponMapper extends Mapper<Coupon> {

    List<Coupon> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("code") Integer code);

    int insertUserGetCoupon(@Param("cp") Coupon coupon);
}