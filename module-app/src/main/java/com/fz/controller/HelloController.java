package com.fz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例控制器类，提供Hello World接口
 */
@RestController
public class HelloController {
    
    /**
     * 提供Hello World的GET请求接口
     * @return 包含Hello World的字符串
     */
    @GetMapping("/app/hello")
    public String hello() {
        return "Hello World from module-app";
    }
}