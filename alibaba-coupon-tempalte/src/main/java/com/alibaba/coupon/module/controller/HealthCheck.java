package com.alibaba.coupon.module.controller;

import com.simple.coupon.common.exception.MyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 健康检查接口
 * @Author: LiuPing
 * @Time: 2020/9/17 0017 -- 14:14
 */
@Slf4j
@RestController
@RequestMapping("/coupon-template")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HealthCheck {

    /**
     * 服务发现客户端
     */
    private final DiscoveryClient client;
    /**
     * 服务注册接口，提供了获取服务的id方法
     */
    private final Registration registration;

    /**
     * 健康检查接口
     * localhost:7001/coupon-template/health
     * http://localhost:8040/nacos-client-coupon-template/coupon-template/health
     *
     * @return
     */
    @GetMapping("/health")
    public String health() {
        log.debug("view heal api");
        return "CouponTempalte is ok !";
    }

    /**
     * 异常测试接口
     * localhost:7001/coupon-template/excp
     * localhost:8040/nacos-client-coupon-template/coupon-template/excp
     */
    @GetMapping("/excp")
    public String testExcp() {
        log.debug("view exception api");
        throw new MyException(505, "CouponTemplate has some problem");
    }

    /**
     * 获取服务注册信息
     * localhost:7001/coupon-template/info
     * localhost:8040/nacos-client-coupon-template/coupon-template/info
     *
     * @return
     */
    @GetMapping("/info")
    public List<?> info() {
        // 大概2分钟获取注册信息
        List<ServiceInstance> instances = client.getInstances(registration.getServiceId());
        List<Map<String, Object>> list = new ArrayList<>(instances.size());

        instances.forEach(
                i -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("serviceId", i.getServiceId());
                    map.put("instanceId", i.getInstanceId());
                    map.put("port", i.getPort());
                    list.add(map);
                }
        );
        return list;
    }
}
