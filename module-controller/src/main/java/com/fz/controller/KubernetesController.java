package com.fz.controller;

import com.fz.service.KubernetesService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/controller/k8s")
public class KubernetesController {
    private static final Logger log = LoggerFactory.getLogger(KubernetesController.class);
    @Autowired
    private CoreV1Api coreV1Api;

    @Autowired
    private AppsV1Api appsV1Api;

    @Autowired
    private KubernetesService kubernetesService;

    // 查询指定命名空间的pod列表
    @GetMapping("/pods")
    public ResponseEntity<?> getPods(
            @RequestParam(name = "namespace", required = false, defaultValue = "default") String namespace
    ) {
        if (namespace == null) {
            namespace = "default";
        }
        List<Pod> pods;
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            pods = client.pods().inNamespace(namespace).list().getItems();
            for (Pod pod : pods) {
                if (pod.getMetadata().getName().contains("module")) {
                    System.out.println("Pod Name: " + pod.getMetadata().getName());
                }
            }
        }
        log.info("getPods namespace: " + namespace + ", pods size: " + pods.size());
        return ResponseEntity.ok(pods);
    }

    // 查询集群中的node列表
    @GetMapping("/nodes")
    public ResponseEntity<?> getNodes() {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            List<io.fabric8.kubernetes.api.model.Node> nodes = client.nodes().list().getItems();
            log.info("getNodes nodes size: " + nodes.size());
            return ResponseEntity.ok(nodes);
        }
    }

    //创建pod举例
    @PostMapping("/createPodCase")
    public ResponseEntity<String> createPodCase() {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            Pod pod = new PodBuilder()
                    .withNewMetadata().withName("nginxcase").endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("nginx")
                    .withImage("nginx:latest")
                    .endContainer()
                    .endSpec()
                    .build();

            client.pods().inNamespace("default").create(pod);
            return ResponseEntity.ok("成功创建pod: " + pod.getMetadata().getName());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建pod失败: " + e.getMessage());
        }
    }
    //删除pod举例
    @PostMapping("/deletePodCase")
    public ResponseEntity<String> deletePodCase() {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            client.pods().inNamespace("default").withName("nginxcase").delete();
            return ResponseEntity.ok("成功删除pod: nginxcase");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除pod失败: " + e.getMessage());
        }
    }

    //创建deployment
    @PostMapping("/createDeployment")
    public ResponseEntity<String> createDeploymentCase() {
        try {
            kubernetesService.createDeployment("default", "nginx-deployment", "nginx:latest", 2);
            return ResponseEntity.ok("成功创建部署 nginx-deployment");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode())
                    .body("创建部署失败: " + e.getResponseBody());
        }
    }

    //删除deployment
    @PostMapping("/deleteDeployment")
    public ResponseEntity<String> deleteDeploymentCase() {
        try {
            kubernetesService.deleteDeployment("default", "nginx-deployment");
            return ResponseEntity.ok("成功删除部署 nginx-deployment");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode())
                    .body("删除部署失败: " + e.getResponseBody());
        }
    }
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
