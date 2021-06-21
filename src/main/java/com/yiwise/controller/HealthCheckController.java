package com.yiwise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口，可应用于监控
 */
@RestController
@RequestMapping({ "/api/health" })
public class HealthCheckController {

    @GetMapping({ "/checkStatus" })
    public String checkStatus() {
        try {
            // TODO 此处可以按需加一些可用性判断的逻辑
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }
}
