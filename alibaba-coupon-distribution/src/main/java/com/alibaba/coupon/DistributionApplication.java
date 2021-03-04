package com.alibaba.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/14 0014 -- 14:55
 */
@EnableFeignClients
@MapperScan("com.alibaba.coupon.module.dao")
@SpringBootApplication
public class DistributionApplication {
    public static void main(String[] args) {
        SpringApplication.run(DistributionApplication.class, args);
    }
}
