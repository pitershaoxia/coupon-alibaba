package com.alibaba.coupon.module.service;

import com.alibaba.coupon.module.enums.RuleFlagEnums;
import com.simple.coupon.domain.dto.SettlementInfoDTO;

/**
 * 优惠券模板规则处理器接口定义
 */
public interface RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return
     */
    RuleFlagEnums ruleConfig();

    /**
     * 优惠券规则计算
     *
     * @param dto
     * @return
     */
    SettlementInfoDTO computeRule(SettlementInfoDTO dto);
}
