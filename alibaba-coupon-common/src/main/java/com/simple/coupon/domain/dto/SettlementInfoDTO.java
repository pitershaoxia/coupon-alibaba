package com.simple.coupon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Auther Piter_Liu
 * @Description 结算信息对象定义
 * @Date 2020/9/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfoDTO {

    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 商品信息
     */
    private List<GoodsInfoDTO> goodsInfos;
    /**
     * 优惠券列表信息
     */
    private List<CouponAndTemplateDTO> couponAndTemplateInfos;
    /**
     * 是否使结算生效,即核销（true）-- 付款结账
     */
    private Boolean employ;
    /**
     * 结算结算金额
     */
    private Double cost;

    /**
     * 优惠券和模板信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponAndTemplateDTO {

        /**
         * coupon的主键
         */
        private Integer id;
        /**
         * 优惠券对应的模板对象
         */
        private CouponTemplateDTO template;
    }
}
