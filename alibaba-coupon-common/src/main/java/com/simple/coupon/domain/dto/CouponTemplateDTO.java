package com.simple.coupon.domain.dto;

import com.simple.coupon.domain.vo.TemplateRuleVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 微服务之间的优惠券模板信息定义
 * @Author: LiuPing
 * @Time: 2020/9/16 0016 -- 17:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateDTO {

    /**
     * 优惠券模板主键
     */
    private Integer id;
    /**
     * 优惠券模板名称
     */
    private String name;
    /**
     * 优惠券logo
     */
    private String logo;
    /**
     * 优惠券描述
     */
    private String desc;
    /**
     * 优惠券分类
     */
    private String category;
    /**
     * 产品线
     */
    private String productLine;
    /**
     * 优惠券模板的编码
     */
    private String key;
    /**
     * 目标用户
     */
    private String target;
    /**
     * 优惠券规则
     */
    private TemplateRuleVo rule;
}
