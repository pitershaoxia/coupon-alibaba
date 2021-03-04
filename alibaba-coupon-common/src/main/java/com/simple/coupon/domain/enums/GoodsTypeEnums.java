package com.simple.coupon.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 商品类型枚举
 */
@Getter
@AllArgsConstructor
public enum GoodsTypeEnums {

    WENYU("文娱类",1),
    SHENGXIAN("生鲜类",2),
    JIAJU("家居类",3),
    OTHERS("其他",4),
    ALL("全品类",5)
    ;

    /**
     * 描述
     */
    private String description;
    /**
     * 编码
     */
    private Integer code;

    public static GoodsTypeEnums getGoodsTypeEnumsBycode(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(b -> b.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "is not exist"));
    }
}
