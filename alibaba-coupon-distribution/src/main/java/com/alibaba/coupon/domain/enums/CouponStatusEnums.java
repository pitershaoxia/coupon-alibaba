package com.alibaba.coupon.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠券状态枚举类
 */
@Getter
@AllArgsConstructor
public enum CouponStatusEnums {

    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期的（未被使用的）", 3);

    /**
     * 优惠券状态描述
     */
    private String description;
    /**
     * 优惠券状态编码
     */
    private Integer code;

    /**
     * 根据code获取到 CouponStatus
     *
     * @return
     */
    public static CouponStatusEnums getCouponStatusByCode(Integer code) {
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(b -> b.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exists"));
    }
}
