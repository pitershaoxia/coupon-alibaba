package com.alibaba.coupon.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simple.coupon.domain.enums.CouponCategoryEnums;
import com.simple.coupon.domain.enums.DistributionEnums;
import com.simple.coupon.domain.enums.ProductLineEnums;
import com.simple.coupon.domain.vo.TemplateRuleVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon_template")
public class CouponTemplate implements Serializable {
    private static final long serialVersionUID = 1904841585003729701L;
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
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
    private CouponCategoryEnums category;

    /**
     * 产品线
     */
    @Column(name = "product_line")
    private ProductLineEnums product_line;

    /**
     * 优惠券总数
     */
    @Column(name = "coupon_count")
    private Integer count;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建用户
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 优惠券模板的编码
     */
    @Column(name = "template_key")
    private String templateKey;

    /**
     * 目标用户
     */
    private DistributionEnums target;

    /**
     * 优惠券规则: TemplateRule 的 json 表示
     */
    private TemplateRuleVo rule;
}