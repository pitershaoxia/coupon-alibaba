package com.simple.coupon.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;


/**
 * 优惠券分类枚举类
 */
@Getter
@AllArgsConstructor
public enum CouponCategoryEnums {

    MANJIAN("满减券", 001),
    ZHEKOU("折扣券", 002),
    LIJIAN("立减券", 003);

    /**
     * 优惠券描述（分类）
     */
    private String description;
    /**
     * 优惠券编码
     */
    private Integer code;

    public static CouponCategoryEnums getCouponByCode(Integer code) {
        Objects.requireNonNull(code);
        // CouponCategoryEnums.values() == values() Java Enums 的语法基础
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "is not exist"));
    }

}
