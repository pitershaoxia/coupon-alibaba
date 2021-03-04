package com.alibaba.coupon.module.feign.hystix;

import com.alibaba.coupon.module.feign.SettlementFeignClient;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Auther Piter_Liu
 * @Description <h1>结算微服务熔断策略实现</h1>
 * @Date 2020/8/8
 */
@Slf4j
@Component
public class SettlementFeignClientHystrix implements SettlementFeignClient {

    /**
     * <h2>优惠券规则计算</h2>
     *
     * @param settlement
     * @return
     */
    @Override
    public SettlementInfoDTO computeRule(SettlementInfoDTO settlement) {
        log.error("【eureka-client-coupon-settlement 服务实例】computeRule " +
                "request error");
        settlement.setEmploy(false);
        settlement.setCost(-1.0);

//        return new RespVo<>(
//                -1,
//                "[eureka-client-coupon-settlement] request error",
//                settlement
//        );
        return settlement;
    }
}
