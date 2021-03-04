package com.alibaba.coupon.domain.vo;

import com.simple.coupon.domain.enums.CouponCategoryEnums;
import com.simple.coupon.domain.enums.DistributionEnums;
import com.simple.coupon.domain.enums.ProductLineEnums;
import com.simple.coupon.domain.vo.TemplateRuleVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @Auther Piter_Liu
 * @Description 优惠券模板参数接收对象
 * @Date 2020/9/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempalteRequestVo {

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
    private String desc;

    /**
     * 优惠券分类
     */
    private Integer category;

    /**
     * 产品线
     */
    private Integer productLine;

    /**
     * 总数
     */
    private Integer count;

    /**
     * 创建用户
     */
    private Long userId;

    /**
     * 目标用户
     */
    private Integer target;

    /**
     * 优惠券规则
     */
    private TemplateRuleVo rule;

    /**
     * 简单的参数校验
     */
    public boolean validate() {
        boolean stringValid = StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);
        boolean enumValid = (null != CouponCategoryEnums.getCouponByCode(category))
                && (null != ProductLineEnums.getProductLineEnumsByCode(productLine))
                && (null != DistributionEnums.getDistributionByCode(target));
        boolean numValid = count > 0 && userId > 0;
        return stringValid && enumValid && numValid && rule.validate();
    }
}
