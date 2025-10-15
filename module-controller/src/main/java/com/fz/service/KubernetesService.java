package com.fz.service;

import com.fz.controller.KubernetesController;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class KubernetesService {
    private static final Logger log = LoggerFactory.getLogger(KubernetesController.class);
    @Autowired
    private CoreV1Api coreV1Api;

    @Autowired
    private AppsV1Api appsV1Api;

    public void scaleDeployment(String namespace, String deploymentName, int replicas) throws ApiException {
        // 获取当前部署
        V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace, null);

        // 更新副本数
        deployment.getSpec().setReplicas(replicas);

        // 应用更改
        appsV1Api.replaceNamespacedDeployment(
                deploymentName,
                namespace,
                deployment,
                null, null, null, null);
    }

    public void updateConfigMap(String namespace, String configMapName, Map<String, String> data) throws ApiException {
        // 获取当前ConfigMap
        V1ConfigMap configMap = coreV1Api.readNamespacedConfigMap(configMapName, namespace, null);

        // 更新数据
        configMap.setData(data);

        // 应用更改
        coreV1Api.replaceNamespacedConfigMap(
                configMapName,
                namespace,
                configMap,
                null, null, null, null);
    }

    public void restartDeployment(String namespace, String deploymentName) throws ApiException {
        // 获取当前部署
        V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace, null);

        // 添加重启注解以触发重新部署
        Map<String, String> annotations = deployment.getSpec().getTemplate().getMetadata().getAnnotations();
        if (annotations == null) {
            annotations = new HashMap<>();
        }

        annotations.put("kubectl.kubernetes.io/restartedAt", Instant.now().toString());
        deployment.getSpec().getTemplate().getMetadata().setAnnotations(annotations);

        // 应用更改
        appsV1Api.replaceNamespacedDeployment(
                deploymentName,
                namespace,
                deployment,
                null, null, null, null);
    }

    public void createDeployment(String namespace, String deploymentName, String image, int replicas) throws ApiException {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            // 在 try 块内，创建客户端之后定义 Deployment
            Deployment deployment = new DeploymentBuilder()
                    .withNewMetadata()
                    .withName(deploymentName) // Deployment 名称
                    .addToLabels("app", deploymentName) // 为 Deployment 添加标签
                    .endMetadata()
                    .withNewSpec()
                    .withReplicas(replicas) // 指定副本数
                    .withNewSelector() // 定义选择器，用于匹配 Pod
                    .addToMatchLabels("app", deploymentName)
                    .endSelector()
                    .withNewTemplate() // 定义 Pod 模板
                    .withNewMetadata()
                    .addToLabels("app", deploymentName) // Pod 标签，需与选择器匹配
                    .endMetadata()
                    .withNewSpec()
                    .addNewContainer() // 定义容器
                    .withName(deploymentName + "-container")
                    .withImage(image) // 替换为你的镜像
                    .addNewPort()
                    .withContainerPort(8080) // 容器暴露的端口
                    .endPort()
                    .endContainer()
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();
            Deployment createdDeployment = client.apps()
                    .deployments()
                    .inNamespace(namespace) // 指定命名空间
                    .create(deployment);
            System.out.println("Deployment created successfully: " +
                    createdDeployment.getMetadata().getName());
        } catch (Exception  e) {
            throw new ApiException(500, "创建deployment失败: " + e.getMessage());
        }
    }

    public void deleteDeployment(String namespace, String deploymentName) throws ApiException {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            // 删除指定的 Deployment 并获取操作结果
            boolean isDeleted = client.apps()
                    .deployments()
                    .inNamespace(namespace)
                    .withName(deploymentName)
                    .delete();

            // 检查删除操作结果
            if (isDeleted) {
                log.info("Deployment '" + deploymentName + "' in namespace '" + namespace + "' was deleted successfully.");
            } else {
                log.warn("Failed to delete Deployment '" + deploymentName + "'. It may not exist.");
            }
        } catch (KubernetesClientException e) {
            // 处理 Kubernetes 客户端相关的异常
//            System.err.println("Kubernetes client error occurred while deleting deployment: " + e.getMessage());
            log.error("Kubernetes client error occurred while deleting deployment: " + e.getMessage());
            throw new ApiException(500, "删除deployment失败: " + e.getMessage());
        } catch (Exception e) {
            // 处理其他可能的异常
//            System.err.println("An unexpected error occurred: " + e.getMessage());
            log.error("An unexpected error occurred: " + e.getMessage());
            throw new ApiException(500, "删除deployment失败: " + e.getMessage());
        }
    }
}
