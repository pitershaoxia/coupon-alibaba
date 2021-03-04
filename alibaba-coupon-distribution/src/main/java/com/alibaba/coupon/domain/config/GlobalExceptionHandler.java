package com.alibaba.coupon.domain.config;

import com.simple.coupon.common.base.ErrorTip;
import com.simple.coupon.common.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: 全局异常类处理器
 * @Author: LiuPing
 * @Time: 2020/9/17 0017 -- 16:55
 */
@Slf4j

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MyException.class)
    @ResponseBody
    public ResponseEntity<ErrorTip> handlerCustomException(MyException e) {
        log.warn("发送MyException异常", e);
//        return ResponseEntity.status(e.getCode()).body(new ErrorTip(e.getCode(),e.getMessage()));
        // 如果要设置返回的header再修改
        return ResponseEntity.status(e.getCode()).body(
                ErrorTip.builder()
                        .message(e.getMessage())
                        .code(e.getCode())
                        .build());
    }

}
