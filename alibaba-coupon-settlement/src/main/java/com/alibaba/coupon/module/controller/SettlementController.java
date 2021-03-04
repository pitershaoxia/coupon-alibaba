package com.alibaba.coupon.module.controller;

import com.alibaba.coupon.module.service.ExecuteManager;
import com.alibaba.fastjson.JSON;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 结算服务  -- 测试ok
 * @Author: LiuPing
 * @Time: 2020/9/25 0025 -- 17:03
 */
@Slf4j
@RestController
@RequestMapping("/coupon-settlement")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SettlementController {

    private final ExecuteManager executeManager;

    @PostMapping("/settlement/compute")
    public SettlementInfoDTO computeGoods(@RequestBody SettlementInfoDTO infoDTO) {
        log.debug("SettlementInfoDTO 传入参数 = {}", JSON.toJSONString(infoDTO));
        SettlementInfoDTO settlementInfoDTO = executeManager.computeRule(infoDTO);
        System.out.println(settlementInfoDTO);
        return settlementInfoDTO;
    }
}
