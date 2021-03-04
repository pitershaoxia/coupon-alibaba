package com.alibaba.coupon;

import com.alibaba.coupon.module.service.ExecuteManager;
import com.alibaba.fastjson.JSON;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.dto.GoodsInfoDTO;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import com.simple.coupon.domain.enums.CouponCategoryEnums;
import com.simple.coupon.domain.enums.GoodsTypeEnums;
import com.simple.coupon.domain.vo.TemplateRuleVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/30 0030 -- 10:19
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class SettlementTest {

    @Autowired
    private ExecuteManager manager;

    private Long fakeUserId = 20001L;

    @Test
    public void test() {

        // 满减优惠券结算测试
        log.info("ManJian Coupon Executor Test");
        SettlementInfoDTO manjianInfo = fakeManJianCouponSettlement();
        SettlementInfoDTO result = manager.computeRule(manjianInfo);
        log.info("manjianInfo = {}", JSON.toJSONString(manjianInfo));
        log.info("result = {}", JSON.toJSONString(result));
        log.info("{}", result.getCost());
        log.info("{}", result.getCouponAndTemplateInfos().size());
        log.info("{}", result.getCouponAndTemplateInfos());

        // 折扣优惠券结算测试
//        log.info("ZheKou Coupon Executor Test");
//        SettlementInfoDTO zhekouInfo = fakeZheKouCouponSettlement();
//        SettlementInfoDTO result = manager.computeRule(zhekouInfo);
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        // 立减优惠券结算测试
//        log.info("LiJian Coupon Executor Test");
//        SettlementInfoDTO lijianInfo = fakeLiJianCouponSettlement();
//        SettlementInfoDTO result = manager.computeRule(lijianInfo);
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        // 满减折扣优惠券结算测试
//        log.info("ManJian ZheKou Coupon Executor Test");
//        SettlementInfoDTO manjianZheKouInfo = fakeManJianAndZheKouCouponSettlement();
//        SettlementInfoDTO result = manager.computeRule(manjianZheKouInfo);
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());
    }


    /**
     * 满减优惠券测试
     *
     * @return
     */
    private SettlementInfoDTO fakeManJianCouponSettlement() {

        SettlementInfoDTO info = new SettlementInfoDTO();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfoDTO GoodsInfoDTO01 = new GoodsInfoDTO();
        GoodsInfoDTO01.setCount(2);
        GoodsInfoDTO01.setPrice(10.88);
        GoodsInfoDTO01.setType(GoodsTypeEnums.WENYU.getCode());

        GoodsInfoDTO GoodsInfoDTO02 = new GoodsInfoDTO();
        // 达到满减标准
        GoodsInfoDTO02.setCount(10);
        // 没有达到满减标准
//        GoodsInfoDTO02.setCount(5);
        GoodsInfoDTO02.setPrice(20.88);
        GoodsInfoDTO02.setType(GoodsTypeEnums.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(GoodsInfoDTO01, GoodsInfoDTO02));

        SettlementInfoDTO.CouponAndTemplateDTO ctInfo =
                new SettlementInfoDTO.CouponAndTemplateDTO();
        ctInfo.setId(1);

        CouponTemplateDTO templateSDK = new CouponTemplateDTO();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategoryEnums.MANJIAN.getCode() + "");
        templateSDK.setKey("100120190801");

        TemplateRuleVo rule = new TemplateRuleVo();
        rule.setDiscount(new TemplateRuleVo.Discount(20, 199));
        rule.setUsage(new TemplateRuleVo.Usage("广州省", "深圳市",
                JSON.toJSONString(Arrays.asList(    // 这里优惠券类型必须包含设置type
                        GoodsTypeEnums.WENYU.getCode(),
                        GoodsTypeEnums.JIAJU.getCode()
                ))));
        templateSDK.setRule(rule);

        ctInfo.setTemplate(templateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));
        System.out.println("传入参数：" + JSON.toJSON(info));
        return info;
    }

    /**
     * 折扣优惠券测试
     *
     * @return
     */
    private SettlementInfoDTO fakeZheKouCouponSettlement() {

        SettlementInfoDTO info = new SettlementInfoDTO();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfoDTO GoodsInfoDTO01 = new GoodsInfoDTO();
        GoodsInfoDTO01.setCount(2);
        GoodsInfoDTO01.setPrice(10.88);
        GoodsInfoDTO01.setType(GoodsTypeEnums.WENYU.getCode());

        GoodsInfoDTO GoodsInfoDTO02 = new GoodsInfoDTO();
        GoodsInfoDTO02.setCount(10);
        GoodsInfoDTO02.setPrice(20.88);
        GoodsInfoDTO02.setType(GoodsTypeEnums.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(GoodsInfoDTO01, GoodsInfoDTO02));

        SettlementInfoDTO.CouponAndTemplateDTO ctInfo =
                new SettlementInfoDTO.CouponAndTemplateDTO();
        ctInfo.setId(1);

        CouponTemplateDTO templateSDK = new CouponTemplateDTO();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategoryEnums.ZHEKOU.getCode() + "");
        templateSDK.setKey("100220190712");

        // 设置 TemplateRule
        TemplateRuleVo rule = new TemplateRuleVo();
        rule.setDiscount(new TemplateRuleVo.Discount(85, 1));
        rule.setUsage(new TemplateRuleVo.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodsTypeEnums.WENYU.getCode(),
                        GoodsTypeEnums.JIAJU.getCode()
                ))));
        // 优惠券类不匹配
//        rule.setUsage(new TemplateRuleVo.Usage("安徽省", "桐城市",
//                JSON.toJSONString(Arrays.asList(
//                        GoodsTypeEnums.SHENGXIAN.getCode(),
//                        GoodsTypeEnums.JIAJU.getCode()
//                ))));

        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));
        System.out.println("传入参数：" + JSON.toJSON(info));
        return info;
    }

    /**
     * 立减优惠券测试
     *
     * @return
     */
    private SettlementInfoDTO fakeLiJianCouponSettlement() {

        SettlementInfoDTO info = new SettlementInfoDTO();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfoDTO GoodsInfoDTO01 = new GoodsInfoDTO();
        GoodsInfoDTO01.setCount(2);
        GoodsInfoDTO01.setPrice(10.88);
        GoodsInfoDTO01.setType(GoodsTypeEnums.WENYU.getCode());

        GoodsInfoDTO GoodsInfoDTO02 = new GoodsInfoDTO();
        GoodsInfoDTO02.setCount(10);
        GoodsInfoDTO02.setPrice(20.88);
        GoodsInfoDTO02.setType(GoodsTypeEnums.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(GoodsInfoDTO01, GoodsInfoDTO02));

        SettlementInfoDTO.CouponAndTemplateDTO ctInfo =
                new SettlementInfoDTO.CouponAndTemplateDTO();
        ctInfo.setId(1);

        CouponTemplateDTO templateSDK = new CouponTemplateDTO();
        templateSDK.setId(3);
        templateSDK.setCategory(CouponCategoryEnums.LIJIAN.getCode() + "");
        templateSDK.setKey("200320190712");

        TemplateRuleVo rule = new TemplateRuleVo();
        rule.setDiscount(new TemplateRuleVo.Discount(5, 1));
        rule.setUsage(new TemplateRuleVo.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodsTypeEnums.WENYU.getCode(),
                        GoodsTypeEnums.JIAJU.getCode()
                ))));
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));
        System.out.println("传入参数：" + JSON.toJSON(info));
        return info;
    }

    /**
     * 满减+折扣优惠券
     *
     * @return
     */
    private SettlementInfoDTO fakeManJianAndZheKouCouponSettlement() {

        SettlementInfoDTO info = new SettlementInfoDTO();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfoDTO GoodsInfoDTO01 = new GoodsInfoDTO();
        GoodsInfoDTO01.setCount(2);
        GoodsInfoDTO01.setPrice(10.88);
        GoodsInfoDTO01.setType(GoodsTypeEnums.WENYU.getCode());

        GoodsInfoDTO GoodsInfoDTO02 = new GoodsInfoDTO();
        GoodsInfoDTO02.setCount(10);
        GoodsInfoDTO02.setPrice(20.88);
        GoodsInfoDTO02.setType(GoodsTypeEnums.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(GoodsInfoDTO01, GoodsInfoDTO02));

        // 满减优惠券
        SettlementInfoDTO.CouponAndTemplateDTO manjianInfo =
                new SettlementInfoDTO.CouponAndTemplateDTO();
        manjianInfo.setId(1);

        CouponTemplateDTO manjianTemplate = new CouponTemplateDTO();
        manjianTemplate.setId(1);
        manjianTemplate.setCategory(CouponCategoryEnums.MANJIAN.getCode() + "");
        // key 必须得符合规则
        manjianTemplate.setKey("100120190712");

        TemplateRuleVo manjianRule = new TemplateRuleVo();
        manjianRule.setDiscount(new TemplateRuleVo.Discount(20, 199));
        manjianRule.setUsage(new TemplateRuleVo.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodsTypeEnums.WENYU.getCode(),
                        GoodsTypeEnums.JIAJU.getCode()
                ))));
        manjianRule.setWeight(JSON.toJSONString(Collections.emptyList()));
        manjianTemplate.setRule(manjianRule);
        manjianInfo.setTemplate(manjianTemplate);

        // 折扣优惠券
        SettlementInfoDTO.CouponAndTemplateDTO zhekouInfo =
                new SettlementInfoDTO.CouponAndTemplateDTO();
        zhekouInfo.setId(1);

        CouponTemplateDTO zhekouTemplate = new CouponTemplateDTO();
        zhekouTemplate.setId(2);
        zhekouTemplate.setCategory(CouponCategoryEnums.ZHEKOU.getCode() + "");
        zhekouTemplate.setKey("100220190712");

        TemplateRuleVo zhekouRule = new TemplateRuleVo();
        zhekouRule.setDiscount(new TemplateRuleVo.Discount(85, 1));
        zhekouRule.setUsage(new TemplateRuleVo.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodsTypeEnums.WENYU.getCode(),
                        GoodsTypeEnums.JIAJU.getCode()
                ))));
        zhekouRule.setWeight(JSON.toJSONString(
                Collections.singletonList("1001201907120001")
        ));
        zhekouTemplate.setRule(zhekouRule);
        zhekouInfo.setTemplate(zhekouTemplate);

        info.setCouponAndTemplateInfos(Arrays.asList(
                manjianInfo, zhekouInfo
        ));
        System.out.println("传入参数：" + JSON.toJSON(info));
        return info;
    }
}
