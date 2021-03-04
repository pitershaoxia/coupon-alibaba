package com.alibaba.coupon.module.service;

import com.alibaba.coupon.domain.dto.AcquireTemplateRequestDTO;
import com.alibaba.coupon.domain.entity.Coupon;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import com.simple.coupon.domain.vo.RespVo;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DistributeService {

    List<Coupon> findCouponByStatus(Long userId, Integer status);

    ResponseEntity<RespVo<?>> findAvailableTemplate(Long userId);

    ResponseEntity<RespVo<?>> acquireTemplate(AcquireTemplateRequestDTO requestDTO);

    SettlementInfoDTO settlement(SettlementInfoDTO infoDTO);
}
