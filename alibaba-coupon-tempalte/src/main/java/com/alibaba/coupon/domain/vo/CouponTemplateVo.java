package com.alibaba.coupon.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateVo {

    /**
     * 自增主键
     */
    private Integer id;

    /**
     * 是否是可用状态; true: 可用, false: 不可用
     */
    private Boolean available;

    /**
     * 是否过期; true: 是, false: 否
     */
    private Boolean expired;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券 logo
     */
    private String logo;

    /**
     * 优惠券描述
     */
    private String intro;

    /**
     * 优惠券分类
     */
    private String category;

    /**
     * 产品线
     */
    private String productLine;

    /**
     * 优惠券总数
     */
    private Integer count;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建用户
     */
    private Long userId;

    /**
     * 优惠券模板的编码
     */
    private String templateKey;

    /**
     * 目标用户
     */
    private String target;

    /**
     * 优惠券规则: TemplateRule 的 json 表示
     */
    private String rule;
}