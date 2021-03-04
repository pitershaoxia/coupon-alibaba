package com.alibaba.coupon.module.service;

import com.alibaba.fastjson.JSON;
import com.simple.coupon.domain.dto.GoodsInfoDTO;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 规则执行器抽象类, 定义通用方法
 * @Author: LiuPing
 * @Time: 2020/9/29 0029 -- 15:28
 */
@SuppressWarnings("all")
public abstract class MyAbstractExecutorService {

    /**
     * 校验商品类型与优惠券是否匹配
     * 需要注意：
     * 1、这里实现的单品类优惠券的校验，多品类的优惠券重载此方法
     * 2、商品只需要一个优惠券要求的商品类型去匹配就可以
     *
     * @return
     */
    protected boolean isGoodsTypeSatisfy(SettlementInfoDTO settlement) {
        List<Integer> goodsTypeList = settlement.getGoodsInfos().stream()
                .map(GoodsInfoDTO::getType)
                .collect(Collectors.toList());
        String goodsType = settlement.getCouponAndTemplateInfos().get(0).getTemplate()
                .getRule().getUsage().getGoodsType();
        List<Integer> templateGoodsTypeList = JSON.parseObject(goodsType, List.class);
        // 存在交集即可
        return CollectionUtils.isNotEmpty(
                CollectionUtils.intersection(goodsTypeList, templateGoodsTypeList));
    }

    /**
     * 处理商品类型与优惠券类型不匹配的情况
     *
     * @param settlement
     * @param goodsSum
     * @return null 是 匹配, 不为null 是不匹配
     */
    protected SettlementInfoDTO processGoodsTypeNotStatisfy(SettlementInfoDTO settlement, Double goodsSum) {
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlement);
        if (!isGoodsTypeSatisfy) {
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        return null;
    }

    /**
     * <h2>商品总价</h2>
     */
    protected double goodsCostSum(List<GoodsInfoDTO> goodsInfos) {

        return goodsInfos.stream().mapToDouble(
                g -> g.getPrice() * g.getCount()
        ).sum();
    }

    /**
     * <h2>保留两位小数</h2>
     */
    protected double retain2Decimals(double value) {

        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    /**
     * <h2>最小支付费用</h2>
     */
    protected double minCost() {
        return 0.1;
    }
}
