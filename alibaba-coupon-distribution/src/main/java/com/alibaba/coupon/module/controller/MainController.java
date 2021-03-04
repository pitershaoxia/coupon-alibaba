package com.alibaba.coupon.module.controller;

import com.alibaba.coupon.domain.dto.AcquireTemplateRequestDTO;
import com.alibaba.coupon.module.service.DistributeService;
import com.alibaba.fastjson.JSON;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import com.simple.coupon.domain.vo.RespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/23 0023 -- 15:56
 */
@Slf4j
@RestController
@RequestMapping("/coupon-distribution")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MainController {

    private final DistributeService distributeService;

    /**
     * 根据用户id和优惠券状态查询用户的优惠券记录
     *
     * @return
     */
    @GetMapping("/coupons")
    public ResponseEntity<RespVo<?>> findCouponByStatus(@RequestParam("userId") Long userId,
                                                        @RequestParam("status") Integer status) {
        log.debug("Find Coupons By Status:{}, {}", userId, status);
        return ResponseEntity.ok(new RespVo<>(distributeService.findCouponByStatus(userId, status)));
    }

    /**
     * 根据用户id 查询当前可以领取的优惠券模板
     *
     * @return
     */
    @GetMapping("/template")
    public ResponseEntity<RespVo<?>> findAvailableTemplate(@RequestParam("userId") Long userId) {
        log.debug("Find Available Template: {}", userId);
        return distributeService.findAvailableTemplate(userId);
    }

    /**
     * 用户领取优惠券
     *
     * @return
     */
    @PostMapping("/acquire/template")
    public ResponseEntity<RespVo<?>> acquireTemplate(@RequestBody AcquireTemplateRequestDTO requestDTO) {
        log.debug("Acquire Template : {}", JSON.toJSONString(requestDTO));
        return distributeService.acquireTemplate(requestDTO);
    }

    /**
     * 结算（核销）优惠券 -- （未测试）
     *
     * @return
     */
    @PostMapping("/settlement")
    public SettlementInfoDTO settlement(@RequestBody SettlementInfoDTO infoDTO) {
        log.debug("settlement 传入参数内容 = {}", JSON.toJSONString(infoDTO));
        return distributeService.settlement(infoDTO);
    }
}
