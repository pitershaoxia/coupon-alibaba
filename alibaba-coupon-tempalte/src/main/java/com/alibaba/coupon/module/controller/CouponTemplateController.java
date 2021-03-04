package com.alibaba.coupon.module.controller;

import com.alibaba.coupon.domain.vo.TempalteRequestVo;
import com.alibaba.coupon.module.service.TemplateService;
import com.alibaba.fastjson.JSON;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.vo.RespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: <h1>优惠券模板相关功能控制器</h1>
 * @Author: LiuPing
 * @Time: 2020/9/18 0018 -- 09:15
 */
@Slf4j
@RestController
@RequestMapping("/coupon-template/template")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CouponTemplateController {

    private final TemplateService templateService;

    /**
     * 构建优惠券模板
     * localhost:7001/coupon-template/template/build
     * localhost:8040/nacos-client-coupon-template/coupon-template/template/build
     *
     * @return
     */
    @PostMapping("/build")
    public ResponseEntity<RespVo<?>> buildTemplate(@RequestBody TempalteRequestVo requestVo) {
        log.debug("【传入构建优惠券模板参数】 = {}", JSON.toJSONString(requestVo));
        return templateService.buildTemplate(requestVo);
    }

    /**
     * 查询指定优惠券模板详情
     * localhost:7001/coupon-template/template/info?id=x
     * localhost:8040/nacos-client-coupon-template/coupon-template/template/info
     *
     * @return
     */
    @GetMapping("/info")
    public ResponseEntity<RespVo<?>> queryTemplateInfo(@RequestParam("id") Integer id) {
        log.debug("【传入查询模板信息ID】= {}", id);
        return templateService.queryTemplateInfo(id);
    }

    /**
     * 查询所有可用的优惠券模板
     * localhost:7001/coupon-template/template/sdk/all
     * localhost:9000/nacos-client-coupon-template/coupon-template/template/sdk/all
     *
     * @return
     */
    @GetMapping("/sdk/all")
    public ResponseEntity<RespVo<?>> findAllUsableTemplate() {
        log.debug("【查询找到所有可用的优惠券模板...】");
        return templateService.findAllUsableTemplate();
    }

    /**
     * 获取模板ids到 coupontemplateDTO的映射
     * localhost:7001/coupon-template/template/sdk/infos？
     * localhost:8040/nacos-client-coupon-template/coupon-template/template/sdk/infos?ids=1,2..
     *
     * @param ids
     * @return
     */
    @GetMapping("/sdk/infos")
    public Map<Integer, CouponTemplateDTO> findIds2TemplateDTO(@RequestParam("ids") List<Integer> ids) {
        log.debug("【获取模板ids到 coupontemplateDTO的映射】= {}", JSON.toJSONString(ids));
        return templateService.findIds2TemplateDTO(ids);
    }

}
