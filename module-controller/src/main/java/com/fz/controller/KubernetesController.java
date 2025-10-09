package com.fz.controller;

import com.fz.service.KubernetesService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/controller/k8s")
public class KubernetesController {
    @Autowired
    private CoreV1Api coreV1Api;

    @Autowired
    private AppsV1Api appsV1Api;

    @Autowired
    private KubernetesService kubernetesService;

    // 扩容/缩容端点
    @PostMapping("/scale/{namespace}/{deploymentName}")
    public ResponseEntity<String> scaleDeployment(
            @PathVariable String namespace,
            @PathVariable String deploymentName,
            @RequestParam int replicas) {

        try {
            kubernetesService.scaleDeployment(namespace, deploymentName, replicas);
            return ResponseEntity.ok("成功将部署 " + deploymentName + " 扩展到 " + replicas + " 个副本");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode())
                    .body("扩展失败: " + e.getResponseBody());
        }
    }

    // 更新配置端点
    @PostMapping("/config/{namespace}/{configMapName}")
    public ResponseEntity<String> updateConfigMap(
            @PathVariable String namespace,
            @PathVariable String configMapName,
            @RequestBody Map<String, String> data) {

        try {
            kubernetesService.updateConfigMap(namespace, configMapName, data);
            return ResponseEntity.ok("成功更新配置映射 " + configMapName);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode())
                    .body("配置更新失败: " + e.getResponseBody());
        }
    }

    // 重启部署端点（应用新配置）
    @PostMapping("/restart/{namespace}/{deploymentName}")
    public ResponseEntity<String> restartDeployment(
            @PathVariable String namespace,
            @PathVariable String deploymentName) {

        try {
            kubernetesService.restartDeployment(namespace, deploymentName);
            return ResponseEntity.ok("成功重启部署 " + deploymentName);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode())
                    .body("重启失败: " + e.getResponseBody());
        }
    }
}
