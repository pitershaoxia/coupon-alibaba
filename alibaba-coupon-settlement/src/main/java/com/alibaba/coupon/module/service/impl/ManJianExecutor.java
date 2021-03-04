package com.alibaba.coupon.module.service.impl;

import com.alibaba.coupon.module.enums.RuleFlagEnums;
import com.alibaba.coupon.module.service.MyAbstractExecutorService;
import com.alibaba.coupon.module.service.RuleExecutor;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/29 0029 -- 16:42
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ManJianExecutor extends MyAbstractExecutorService implements RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return
     */
    @Override
    public RuleFlagEnums ruleConfig() {
        return RuleFlagEnums.MANJIAN;
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
            log.debug("商品信息与满减优惠券类型不匹配");
            return statisfy;
        }
        // 满足条件在判断满减是否符合折扣的标准
        CouponTemplateDTO templateDTO = dto.getCouponAndTemplateInfos().get(0).getTemplate();
        double base = templateDTO.getRule().getDiscount().getBase();
        double quota = templateDTO.getRule().getDiscount().getQuota();
        // 如果总价不符合标准没达到满减额度,直接返回总价
        if (goodsSum < base) {
            log.debug("当前商品总价 < 满减优惠券的折扣标准");
            dto.setCost(goodsSum);
            dto.setCouponAndTemplateInfos(Collections.emptyList());
            return dto;
        }
        // 计算使用优惠券之后的价格 -- 结算
        dto.setCost(
                retain2Decimals((goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost())
        );
        log.debug("使用满减优惠券后的商品总价从{} to {}", goodsSum, dto.getCost());
        return dto;
    }
}
