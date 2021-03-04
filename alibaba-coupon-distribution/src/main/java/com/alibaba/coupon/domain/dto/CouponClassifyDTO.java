package com.alibaba.coupon.domain.dto;

import com.alibaba.coupon.domain.entity.Coupon;
import com.alibaba.coupon.domain.enums.CouponStatusEnums;
import com.simple.coupon.domain.enums.PeriodTypeEnums;
import com.simple.coupon.domain.vo.TemplateRuleVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 用户优惠券分类，根据优惠券状态
 * @Author: LiuPing
 * @Time: 2020/9/23 0023 -- 14:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassifyDTO {

    /**
     * 可使用的
     */
    private List<Coupon> usableList;
    /**
     * 已使用的
     */
    private List<Coupon> usedList;
    /**
     * 已过期的
     */
    private List<Coupon> expireList;

    /**
     * 对当前的优惠券进行分类
     *
     * @return
     */
    @SuppressWarnings("all")
    public static CouponClassifyDTO classify(List<Coupon> coupons) {
        List<Coupon> usableList = new ArrayList<>(coupons.size());
        List<Coupon> usedList = new ArrayList<>(coupons.size());
        List<Coupon> expireList = new ArrayList<>(coupons.size());

        // 优惠券本身的过期策略是延时的
        coupons.forEach(
                c -> {
                    // 判断优惠券是否过期
                    Boolean isTimeExpire;   // true -- 过期
                    long curTime = new Date().getTime();
                    TemplateRuleVo.Expiration ex = c.getTemplateSDK().getRule().getExpiration();
                    if (PeriodTypeEnums.REGULAR.getCode().equals(ex.getPeriod())) {
                        // 如果是固定日期，只要判断一下当前的优惠券deadline 是否小于当前世界
                        isTimeExpire = ex.getDeadline() <= curTime;
                    } else {
                        // 变动日期
                        isTimeExpire = DateUtils.addDays(
                                c.getAssignTime(),  // 用户领取优惠券的时间
                                ex.getGap() // 时间间隔
                        ).getTime() <= curTime; // 对时间操作后与当前世界进行判断
                    }
                    if (CouponStatusEnums.USED == c.getStatus()) {
                        usedList.add(c);
                    } else if (CouponStatusEnums.EXPIRED == c.getStatus()) {
                        expireList.add(c);
                    } else {
                        usableList.add(c);
                    }
                }
        );

        return CouponClassifyDTO.builder()
                .usableList(usableList)
                .usedList(usedList)
                .expireList(expireList)
                .build();
    }
}
