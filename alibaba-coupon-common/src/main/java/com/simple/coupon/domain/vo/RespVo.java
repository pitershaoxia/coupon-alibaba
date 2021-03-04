package com.simple.coupon.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/14 0014 -- 16:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespVo<T> implements Serializable {

    private static final long serialVersionUID = -7395558874666977196L;

    private Integer code;
    private String msg;
    private T data;

    public RespVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespVo(T data) {
        this.code = 200;
        this.data = data;
        this.msg = "";
    }
}
