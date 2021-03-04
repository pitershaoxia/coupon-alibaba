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
 * @Time: 2020/9/29 0029 -- 17:03
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ZheKouExecutor extends MyAbstractExecutorService implements RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return
     */
    @Override
    public RuleFlagEnums ruleConfig() {
        return RuleFlagEnums.ZHEKOU;
    }

    /**
     * 优惠券计算规则
     *
     * @param dto
     * @return
     */
    @Override
    public SettlementInfoDTO computeRule(SettlementInfoDTO dto) {
        double goodsSum = retain2Decimals(goodsCostSum(dto.getGoodsInfos()));
        SettlementInfoDTO statisfy = processGoodsTypeNotStatisfy(dto, goodsSum);
        if (statisfy != null) {
            log.debug("折扣优惠券不匹配商品信息");
            return statisfy;
        }
        // 折扣优惠券可以直接使用,没有其他限制
        CouponTemplateDTO templateDTO = dto.getCouponAndTemplateInfos().get(0).getTemplate();
        double quota = templateDTO.getRule().getDiscount().getQuota();
        // 计算使用优惠券之后的价格
        dto.setCost(
                retain2Decimals(
                        (goodsSum * (quota * 1.0 / 100) > minCost())
                                ? retain2Decimals((goodsSum * (quota * 1.0 / 100))) : minCost()
                )
        );
        log.info("使用折扣券后的商品总价cost从= {} To {}", goodsSum, dto.getCost());
        return dto;
    }
}
