package com.alibaba.coupon.module.service;

import com.alibaba.coupon.domain.vo.TempalteRequestVo;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.vo.RespVo;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface TemplateService {

    ResponseEntity<RespVo<?>> buildTemplate(TempalteRequestVo requestVo);

    ResponseEntity<RespVo<?>> queryTemplateInfo(Integer id);

    ResponseEntity<RespVo<?>> findAllUsableTemplate();

    Map<Integer, CouponTemplateDTO> findIds2TemplateDTO(List<Integer> ids);
}
