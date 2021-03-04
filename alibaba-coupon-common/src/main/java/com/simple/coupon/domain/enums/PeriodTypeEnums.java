package com.simple.coupon.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 有效期类型枚举
 */
@Getter
@AllArgsConstructor
public enum PeriodTypeEnums {

    REGULAR("固定的（固定日期）",1),
    SHIFT("变动的（以领取之日开始计算）",2),
    ;

    /**
     * 描述
     */
    private String description;
    /**
     * 编码
     */
    private Integer code;

    public static PeriodTypeEnums getPeriodTypeEnumsByCode(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(b -> b.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "is not exist"));
    }
}
