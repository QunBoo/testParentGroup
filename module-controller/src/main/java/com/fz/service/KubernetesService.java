package com.fz.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class KubernetesService {
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
}
