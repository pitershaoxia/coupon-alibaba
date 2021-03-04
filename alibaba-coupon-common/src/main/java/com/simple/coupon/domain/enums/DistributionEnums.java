package com.simple.coupon.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠券分发目标枚举类
 */
@Getter
@AllArgsConstructor
public enum DistributionEnums {

    SINGLE("单个用户", 1),
    MULTI("多用户", 2);

    /**
     * 描述
     */
    private String description;
    /**
     * 编码
     */
    private Integer code;


    public static DistributionEnums getDistributionByCode(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(b -> b.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "is not exist"));
    }

}
