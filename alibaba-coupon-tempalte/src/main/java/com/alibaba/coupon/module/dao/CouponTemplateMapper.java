package com.alibaba.coupon.module.dao;

import com.alibaba.coupon.domain.entity.CouponTemplate;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponTemplateMapper extends Mapper<CouponTemplate> {

    int insertTemplate(@Param("ct") CouponTemplate ct);

    CouponTemplate queryTemplateById(Integer id);

    int updateAvailable(@Param("ct") CouponTemplate ct);

    List<CouponTemplate> findAllUsableTemplate();

    List<CouponTemplate> findAllExpiredTemplate();

    int updateExpireTemplate(@Param("template") CouponTemplate template);
}