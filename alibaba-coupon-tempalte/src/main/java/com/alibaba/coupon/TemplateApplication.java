package com.alibaba.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Description: 模板微服务的启动类
 * @Author: LiuPing
 * @Time: 2020/9/14 0014 -- 11:50
 */
@EnableScheduling   // 开启定时任务
@EnableFeignClients
@MapperScan("com.alibaba.coupon.module.dao")
@SpringBootApplication
public class TemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }
}
