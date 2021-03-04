package com.alibaba.coupon.module.feign;

import com.alibaba.coupon.module.feign.hystix.SettlementFeignClientHystrix;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 结算微服务的 feign 接口定义
 */
@FeignClient(value = "nacos-client-coupon-settlement", fallback = SettlementFeignClientHystrix.class)
public interface SettlementFeignClient {

    /**
     * <h2>优惠券规则计算</h2>
     */
    @RequestMapping(value = "/coupon-settlement/settlement/compute",
            method = RequestMethod.POST)
    SettlementInfoDTO computeRule(
            @RequestBody SettlementInfoDTO settlement);
}
