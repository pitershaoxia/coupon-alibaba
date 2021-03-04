package com.simple.coupon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther Piter_Liu
 * @Description 商品信息
 * @Date 2020/9/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfoDTO {

    /**
     * 商品类型
     */
    private Integer type;
    /**
     * 商品价格
     */
    private Double price;
    /**
     * 商品数量
     */
    private Integer count;
}
