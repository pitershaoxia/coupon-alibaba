package com.alibaba.coupon.module.service.impl;

import com.alibaba.coupon.module.enums.RuleFlagEnums;
import com.alibaba.coupon.module.service.MyAbstractExecutorService;
import com.alibaba.coupon.module.service.RuleExecutor;
import com.alibaba.fastjson.JSON;
import com.simple.coupon.domain.dto.GoodsInfoDTO;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import com.simple.coupon.domain.enums.CouponCategoryEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 满减 ＋　折扣优惠券结算规则执行器
 * @Author: LiuPing
 * @Time: 2020/9/29 0029 -- 17:08
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ManJianZheKouExecutor extends MyAbstractExecutorService implements RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return
     */
    @Override
    public RuleFlagEnums ruleConfig() {
        return RuleFlagEnums.MANJIAN_ZHEKOU;
    }

    /**
     * 校验商品类型与优惠券是否匹配
     * 需要注意：
     * 1、这里实现的满减 +　折扣优惠券的校验
     * 2、如果想要使用多类优惠券,则必须要所有的商品类型都包含在内，即差集为空
     *
     * @param dto
     * @return
     */
    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfoDTO settlement) {
        log.debug("校验满减和折扣优惠券是否匹配满足条件");
        List<Integer> goodsTypeList = settlement.getGoodsInfos().stream()
                .map(GoodsInfoDTO::getType)
                .collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();
        settlement.getCouponAndTemplateInfos().forEach(
                c -> {
                    templateGoodsType.addAll(JSON.parseObject(
                            c.getTemplate().getRule().getUsage().getGoodsType(),
                            List.class
                    ));
                }
        );
        // 如果想要使用多种优惠券叠加，则必须要所有商品的类型都包含在内，即差集为空,
        return CollectionUtils.isEmpty(
                CollectionUtils.subtract(goodsTypeList, templateGoodsType)   // A - B 为null A的集合比B小
        );
    }


    /**
     * 优惠券规则的计算
     *
     * @param dto 包含了选择的优惠券
     * @return
     */
    @Override
    public SettlementInfoDTO computeRule(SettlementInfoDTO dto) {
        double goodsSum = retain2Decimals(goodsCostSum(dto.getGoodsInfos()));
        // 商品类型的校验
        SettlementInfoDTO statisfy = processGoodsTypeNotStatisfy(dto, goodsSum);
        if (statisfy != null) {
            log.debug("满减优惠券和折扣优惠券模板不匹配当前的商品类型");
            return statisfy;
        }
        SettlementInfoDTO.CouponAndTemplateDTO manjian = null;
        SettlementInfoDTO.CouponAndTemplateDTO zhekou = null;


        for (SettlementInfoDTO.CouponAndTemplateDTO ct : dto.getCouponAndTemplateInfos()) {
            if (CouponCategoryEnums.getCouponByCode(Integer.valueOf(ct.getTemplate().getCategory())) == CouponCategoryEnums.MANJIAN) {
                manjian = ct;
            } else {
                zhekou = ct;
            }
        }
        assert null != manjian;
        assert null != zhekou;
        // 当前的折扣优惠券和满减券如果不能共用（一起使用），则清空优惠券，返回商品原价
        if (!isTemplateCanShared(manjian, zhekou)) {
            log.debug("当前的满减券和折扣券不能被一起使用");
            dto.setCost(goodsSum);
            dto.setCouponAndTemplateInfos(Collections.emptyList());
            return dto;
        }
        List<SettlementInfoDTO.CouponAndTemplateDTO> ctInfos = new ArrayList<>();
        double manjianBase = manjian.getTemplate().getRule().getDiscount().getBase();
        double manjianQuota = manjian.getTemplate().getRule().getDiscount().getQuota();

        // 最终价格
        double tatgetSum = goodsSum;
        // 先计算满减
        if (tatgetSum >= manjianBase) {
            tatgetSum -= manjianQuota;
            ctInfos.add(manjian);
        }
        // 再计算折扣
        double zhekouQuota = zhekou.getTemplate().getRule().getDiscount().getQuota();
        tatgetSum *= zhekouQuota * 1.0 / 100;
        ctInfos.add(zhekou);

        dto.setCost(retain2Decimals(
                tatgetSum > minCost() ? tatgetSum : minCost()
        ));
        dto.setCouponAndTemplateInfos(ctInfos);
        log.info("使用满减券和折扣券后的商品总价cost = {} To {}", goodsSum, dto.getCost());
        return dto;
    }

    private boolean isTemplateCanShared(SettlementInfoDTO.CouponAndTemplateDTO manJian, SettlementInfoDTO.CouponAndTemplateDTO zheKou) {

        // 优惠券编码的id4位
        String manjianKey = manJian.getTemplate().getKey()
                + String.format("%04d", manJian.getTemplate().getId());
        String zhekouKey = zheKou.getTemplate().getKey()
                + String.format("%04d", zheKou.getTemplate().getId());

        List<String> allSharedKeysForManjian = new ArrayList<>();
        allSharedKeysForManjian.add(manjianKey);
        allSharedKeysForManjian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(),
                List.class
        ));

        List<String> allSharedKeysForZhekou = new ArrayList<>();
        allSharedKeysForZhekou.add(zhekouKey);
        allSharedKeysForZhekou.addAll(JSON.parseObject(
                zheKou.getTemplate().getRule().getWeight(),
                List.class
        ));

        return CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey, zhekouKey), allSharedKeysForManjian)
                || CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey, zhekouKey), allSharedKeysForZhekou);
    }
}
