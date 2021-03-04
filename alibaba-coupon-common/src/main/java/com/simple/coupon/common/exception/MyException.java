package com.simple.coupon.common.exception;

import lombok.*;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/16 0016 -- 16:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyException extends RuntimeException {

    private Integer code;
    private String message;

}
