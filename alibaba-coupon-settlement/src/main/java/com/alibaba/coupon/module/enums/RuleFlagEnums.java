package com.alibaba.coupon.module.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Auther Piter_Liu
 * @Description <h1>规则类型枚举定义</h1>
 * @Date 2020/9/25
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("all")
public enum RuleFlagEnums {

    MANJIAN("满减券的计算规则"),
    ZHEKOU("折扣券的计算规则"),
    LIJIAN("立减券的计算规则"),

    MANJIAN_ZHEKOU("满减券+折扣券的计算规则");

    /**
     * 规则描述
     */
    private String description;
}
