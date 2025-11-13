package com.fz.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 示例控制器类，提供Hello World接口
 */
@RestController
public class HelloController {
    
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    
    /**
     * 提供Hello World的GET请求接口
     * @return 包含Hello World的字符串
     */
    @GetMapping("/operator/hello")
    public String hello() {
        logger.info("Hello接口被调用");
        return "Hello World From /api/hello";
    }
}