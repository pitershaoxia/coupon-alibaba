package com.simple.coupon.common.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 返回给前端的提示信息
 * @Author: LiuPing
 * @Time:2020/4/27 0027 15:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorTip {

    protected int code;
    protected String message;
}
