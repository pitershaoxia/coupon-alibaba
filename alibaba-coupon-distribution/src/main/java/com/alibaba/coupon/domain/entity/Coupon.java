package com.alibaba.coupon.domain.entity;

import com.alibaba.coupon.domain.enums.CouponStatusEnums;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon")
public class Coupon {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 关联优惠券模板的主键
     */
    @Column(name = "template_id")
    private Integer templateId;

    /**
     * 领取用户
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 优惠券码
     */
    @Column(name = "coupon_code")
    private String couponCode;

    /**
     * 领取时间
     */
    @Column(name = "assign_time")
    private Date assignTime;

    /**
     * 优惠券的状态(1-可用，2-已使用，3-过期)
     */
    private CouponStatusEnums status;

    /**
     * 用户优惠券对应的模板信息
     */
    @Transient
    private CouponTemplateDTO templateSDK;

    /**
     * 返回一个无效的coupon对象
     *
     * @return
     */
    public static Coupon invalidCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

}