package com.alibaba.coupon.module.service.impl;

import com.alibaba.coupon.domain.entity.CouponTemplate;
import com.alibaba.coupon.domain.vo.CouponTemplateVo;
import com.alibaba.coupon.domain.vo.TempalteRequestVo;
import com.alibaba.coupon.module.dao.CouponTemplateMapper;
import com.alibaba.coupon.module.service.MyAsyncService;
import com.alibaba.coupon.module.service.TemplateService;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.simple.coupon.common.exception.MyException;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.enums.CouponCategoryEnums;
import com.simple.coupon.domain.enums.DistributionEnums;
import com.simple.coupon.domain.enums.ProductLineEnums;
import com.simple.coupon.domain.vo.RespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/17 0017 -- 14:13
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TemplateServiceImpl implements TemplateService {

    private final CouponTemplateMapper templateMapper;
    private final MyAsyncService asyncService;

    /**
     * 构建优惠券模板
     *
     * @return
     */
    @Override
    public ResponseEntity<RespVo<?>> buildTemplate(TempalteRequestVo requestVo) {

        // 简单校验参数的合法性
        if (!requestVo.validate()) {
            throw new MyException(400, "构建模板的参数无效，请仔细检查");
        }

        // 检查模板库是否存在相同模板
        CouponTemplate couponTemplate = templateMapper.selectOne(
                CouponTemplate.builder()
                        .name(requestVo.getName())
                        .build()
        );
        if (couponTemplate != null) {
            throw new MyException(400, "已存在相同的模板");
        }

        // 创建新模板
        Integer category = requestVo.getCategory();
        Integer productLine = requestVo.getProductLine();

        CouponTemplate template = CouponTemplate.builder()
                .available(false)
                .expired(false)
                .name(requestVo.getName())
                .logo(requestVo.getLogo())
                .intro(requestVo.getDesc())
                .category(CouponCategoryEnums.getCouponByCode(category))    // 类型转换存code
                .product_line(ProductLineEnums.getProductLineEnumsByCode(productLine))    // 类型转换存code
                .count(requestVo.getCount())
                .userId(requestVo.getUserId())
                .createTime(new Date())
                // 这个key还得修改
                .templateKey(productLine + category + new SimpleDateFormat("yyyyMMdd").format(new Date()) + RandomStringUtils.randomNumeric(4))
                .target(DistributionEnums.getDistributionByCode(requestVo.getTarget()))
                .rule(requestVo.getRule())  // 类型转换存json字符串
                .build();

        templateMapper.insertTemplate(template);

        // 根据优惠券模板异步生成优惠券码，并标识为模板可用
        asyncService.asyncBuildCouponByTemplate(template);
        return ResponseEntity.ok(new RespVo<>(template));
    }

    /**
     * 根据优惠券模板id 获取优惠券模板信息
     *
     * @return
     */
    @Override
    public ResponseEntity<RespVo<?>> queryTemplateInfo(Integer id) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        CouponTemplate couponTemplate = templateMapper.queryTemplateById(id);
        if (couponTemplate == null) {
            throw new MyException(501, "查询的模板不存在:" + id);
        }
        CouponTemplateVo vo = new CouponTemplateVo();
        BeanUtils.copyProperties(couponTemplate, vo);
        vo.setCategory(couponTemplate.getCategory().getDescription());
        vo.setTarget(couponTemplate.getTarget().getDescription());
        vo.setProductLine(couponTemplate.getProduct_line().getDescription());
        vo.setRule(JSON.toJSONString(couponTemplate.getRule()));
        stopwatch.stop();
        log.debug("【查询优惠券模板信息({})耗时】= {}ms", id, stopwatch.elapsed(TimeUnit.MICROSECONDS));
        return ResponseEntity.ok(new RespVo<>(vo));
    }

    /**
     * 查询所有可用的优惠券模板
     *
     * @return
     */
    @Override
    public ResponseEntity<RespVo<?>> findAllUsableTemplate() {
        List<CouponTemplate> templates = templateMapper.findAllUsableTemplate();

        // 将CouponTemplate类型转换成CouponTemplateDTO类型
        List<CouponTemplateDTO> list = templates.stream()
                .map(this::tempalte2TemplateDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new RespVo<>(list));
    }

    /**
     * 获取模板ids到 coupontemplateDTO的映射
     *
     * @param ids
     * @return
     */
    @Override
    public Map<Integer, CouponTemplateDTO> findIds2TemplateDTO(List<Integer> ids) {
        List<CouponTemplate> templates = new ArrayList<>();
        for (Integer id : ids) {
            templates.add(templateMapper.queryTemplateById(id));
        }
        // 将CouponTemplate类型转换CouponTemplateDTO
        return templates.stream()
                .map(this::tempalte2TemplateDTO)
                .collect(Collectors.toMap(
                        CouponTemplateDTO::getId, Function.identity()   // map 的key是模板的id
                ));
    }

    /**
     * 类型转换
     *
     * @param template
     * @return
     */
    private CouponTemplateDTO tempalte2TemplateDTO(CouponTemplate template) {
        return CouponTemplateDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .logo(template.getLogo())
                .desc(template.getIntro())
                .category(template.getCategory().getDescription())
                .productLine(template.getProduct_line().getDescription())
                .key(template.getTemplateKey())
                .target(template.getTarget().getDescription())
                .rule(template.getRule())
                .build();
    }

}
