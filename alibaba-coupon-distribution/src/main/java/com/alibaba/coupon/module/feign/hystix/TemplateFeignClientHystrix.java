package com.alibaba.coupon.module.feign.hystix;

import com.alibaba.coupon.module.feign.TemplateFeignClient;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Auther Piter_Liu
 * @Description <h1>优惠券模板feign 接口的熔断降级策略</h1>
 * @Date 2020/8/8
 */
@Slf4j
@Component
public class TemplateFeignClientHystrix implements TemplateFeignClient {


    /**
     * <h2>查询所有可用的优惠券模板</h2>
     */
    @Override
    public RespVo<List<CouponTemplateDTO>> findAllUsableTemplate() {
        log.error("【eureka-client-coupon-template 服务实例】 findAllUsableTemplate " +
                "request error");
        // 出现熔断，返回空数据
        return new RespVo<>(
                -1,
                "[eureka-client-coupon-template] request error",
                Collections.emptyList()
        );
    }

    /**
     * <h2>获取模板ids 到 CouponTemplateDTO 的映射</h2>
     */
    @Override
    public Map<Integer, CouponTemplateDTO> findIds2TemplateDTO(List<Integer> ids) {
        log.error("【eureka-client-coupon-template 服务实例】findIds2TemplateDTO " +
                "request error");

//        return new RespVo<>(
//                -1,
//                "[eureka-client-coupon-template] request error",
//                Collections.emptyMap()
//        );
        return Collections.emptyMap();
    }
}
