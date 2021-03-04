package com.alibaba.coupon.domain.dto;

import com.simple.coupon.domain.dto.CouponTemplateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 获取优惠券请求对象定义
 * @Author: LiuPing
 * @Time: 2020/9/23 0023 -- 14:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcquireTemplateRequestDTO {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 优惠券模板信息
     */
    private CouponTemplateDTO templateDTO;

}
