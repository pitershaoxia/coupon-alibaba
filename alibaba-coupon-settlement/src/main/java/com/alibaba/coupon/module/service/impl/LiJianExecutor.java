package com.alibaba.coupon.module.service.impl;

import com.alibaba.coupon.module.enums.RuleFlagEnums;
import com.alibaba.coupon.module.service.MyAbstractExecutorService;
import com.alibaba.coupon.module.service.RuleExecutor;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/29 0029 -- 15:07
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class LiJianExecutor extends MyAbstractExecutorService implements RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return
     */
    @Override
    public RuleFlagEnums ruleConfig() {
        return RuleFlagEnums.LIJIAN;
    }

    /**
     * 优惠券规则计算
     *
     * @param dto 包含了选择的优惠券
     * @return 修正过的结算信息
     */
    @Override
    public SettlementInfoDTO computeRule(SettlementInfoDTO dto) {
        double goodsSum = retain2Decimals(goodsCostSum(dto.getGoodsInfos()));
        SettlementInfoDTO statisfy = processGoodsTypeNotStatisfy(dto, goodsSum);
        if (statisfy != null) {
            log.debug("立减券模板与当前商品信息不匹配");
            return statisfy;
        }
        // 立减优惠券直接使用,无其他限制
        CouponTemplateDTO templateDTO =
                dto.getCouponAndTemplateInfos().get(0).getTemplate();
        double quota = templateDTO.getRule().getDiscount().getQuota();
        // 计算使用优惠券之后的价格--结算
        dto.setCost(
                retain2Decimals(goodsSum - quota) > minCost()
                        ? retain2Decimals(goodsSum - quota) : minCost()
        );
        log.info("使用立减券后的商品总价cost从 = {} to {}", goodsSum, dto.getCost());
        return dto;
    }

}
