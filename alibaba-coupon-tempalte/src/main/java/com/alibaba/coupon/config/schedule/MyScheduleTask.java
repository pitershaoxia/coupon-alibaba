package com.alibaba.coupon.config.schedule;

import com.alibaba.coupon.domain.entity.CouponTemplate;
import com.alibaba.coupon.module.dao.CouponTemplateMapper;
import com.simple.coupon.domain.vo.TemplateRuleVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther Piter_Liu
 * @Description 自定义定时清理已过期的优惠券模板
 * @Date 2020/9/16
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MyScheduleTask {

    private final CouponTemplateMapper templateMapper;

    @Scheduled(fixedRate = 60 * 60 * 1000) // 每一小时执行一次
    public void cleanLineCouponTemplate() {
        log.debug("开始清理过期优惠券...");
        // 先查询当前模板系统中未过期的优惠券template
        List<CouponTemplate> templates = templateMapper.findAllExpiredTemplate();

        if (CollectionUtils.isEmpty(templates)) {
            log.debug("已经完成过期优惠券模板的清理");
            return;
        }
        // 准备开始清理
        Date now = new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());
        templates.forEach(
                t -> {
                    // 根据优惠券规则中的"过期规则" 校验模板是否已经过期
                    TemplateRuleVo rule = t.getRule();
                    if (rule.getExpiration().getDeadline() < now.getTime()) {
                        // 比较时间
                        t.setExpired(true);
                        expiredTemplates.add(t);
                    }
                }
        );

        if (CollectionUtils.isNotEmpty(expiredTemplates)) {
            for (CouponTemplate template : expiredTemplates) {
                templateMapper.updateExpireTemplate(template);
            }
            log.debug("【过期的优惠券模板数量】= {}", expiredTemplates.size());
        }

        log.info("【已经完成过期优惠券模板的清理....】");
    }
}
