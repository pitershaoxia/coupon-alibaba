package com.alibaba.coupon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: <h1>优惠券 kafka 消息对象定义</h1>
 * @Author: LiuPing
 * @Time: 2020/8/6 0006 -- 17:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessageDTO {

    /**
     * 优惠券状态
     */
    private Integer status;

    /**
     * coupon 的主键
     */
    private List<Integer> ids;
}
