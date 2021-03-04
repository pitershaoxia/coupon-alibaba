package com.alibaba.coupon.module.feign;

import com.alibaba.coupon.module.feign.hystix.TemplateFeignClientHystrix;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.vo.RespVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 优惠券模板微服务 feign 接口的定义
 */
@FeignClient(value = "nacos-client-coupon-template", fallback = TemplateFeignClientHystrix.class)
public interface TemplateFeignClient {

    /**
     * 查询所有可用的优惠券模板
     *
     * @return
     */
    @GetMapping("/coupon-template/template/sdk/all")
    RespVo<List<CouponTemplateDTO>> findAllUsableTemplate();

    /**
     * 获取模板ids 到 CouponTemplateDTO 的映射
     *
     * @return
     */
    @GetMapping("/coupon-template/template/sdk/infos")
    Map<Integer, CouponTemplateDTO> findIds2TemplateDTO(@RequestParam("ids") List<Integer> ids);

}
