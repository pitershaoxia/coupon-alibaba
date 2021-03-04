package com.alibaba.coupon.module.service;

import com.alibaba.coupon.module.enums.RuleFlagEnums;
import com.simple.coupon.common.exception.MyException;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import com.simple.coupon.domain.enums.CouponCategoryEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: <h1>优惠券结算规则执行管理器</h1>
 * 即根据用户的请求(SettlementInfo)找到对应的 Executor, 去做结算
 * BeanPostProcessor: Bean 后置处理器 -- 它是在spring容器完成初始化后执行的
 * @Author: LiuPing
 * @Time: 2020/9/25 0025 -- 17:09
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ExecuteManager implements BeanPostProcessor {

    /* 规则执行器映射 */
    private static Map<RuleFlagEnums, RuleExecutor> executorIndex =
            new HashMap<>(RuleFlagEnums.values().length);

    /**
     * 优惠券结算规则计算入口
     * 注意：一定要保证传递进来的优惠券数量 >= 1
     *
     * @param infoDTO
     * @return
     */
    public SettlementInfoDTO computeRule(SettlementInfoDTO dto) {
        SettlementInfoDTO result = null;
        // 单类的优惠券
        if (dto.getCouponAndTemplateInfos().size() == 1) {
            // 获取优惠券的类别 (下发的参数传递有时是数值，有时是文字，有时间再优化)
            CouponCategoryEnums couponByCode = CouponCategoryEnums.getCouponByCode(Integer.valueOf(
                    dto.getCouponAndTemplateInfos().get(0).getTemplate().getCategory()));
            switch (couponByCode) {
                case MANJIAN:
                    result = executorIndex.get(RuleFlagEnums.MANJIAN).computeRule(dto);
                    break;
                case LIJIAN:
                    result = executorIndex.get(RuleFlagEnums.LIJIAN).computeRule(dto);
                    break;
                case ZHEKOU:
                    result = executorIndex.get(RuleFlagEnums.ZHEKOU).computeRule(dto);
                    break;
            }
        } else {
            // 多类优惠券
            List<CouponCategoryEnums> categoryList = new ArrayList<>(dto.getCouponAndTemplateInfos().size());
            dto.getCouponAndTemplateInfos().forEach(
                    c -> categoryList.add(CouponCategoryEnums.getCouponByCode(
                            Integer.valueOf(c.getTemplate().getCategory())))
            );
            if (categoryList.size() != 2) {
                throw new MyException(400, "Not support for more template category");
            } else {
                // 针对自定义的规则
                if (categoryList.contains(CouponCategoryEnums.MANJIAN) && categoryList.contains(CouponCategoryEnums.ZHEKOU)) {
                    result = executorIndex.get(RuleFlagEnums.MANJIAN_ZHEKOU).computeRule(dto);
                } else {
                    throw new MyException(400, "Not support for more template category");
                }
            }
        }
        return result;
    }

    /**
     * 在bean 初始化之前执行
     *
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof RuleExecutor)) {
            // 不是 RuleExecutor 的实例直接返回
            return bean;
        }
        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlagEnums ruleConfig = executor.ruleConfig();
        if (executorIndex.containsKey(ruleConfig)) {
            throw new IllegalStateException("There is already an executor for rule flag :" + ruleConfig);
        }
        log.info("Load Executor {} for rule flag {}", executor.getClass(), ruleConfig);
        executorIndex.put(ruleConfig, executor);
        return null;
    }

    /**
     * 在bean 初始化之后执行
     *
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
