package com.simple.coupon.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 产品线枚举
 */
@Getter
@AllArgsConstructor
public enum ProductLineEnums {

    TIANMAO("天猫", 1),
    TAOBAO("淘宝", 2),
    ;

    /**
     * 描述
     */
    private String description;
    /**
     * 编码
     */
    private Integer code;

    public static ProductLineEnums getProductLineEnumsByCode(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(b -> b.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "is not exist"));
    }
}
