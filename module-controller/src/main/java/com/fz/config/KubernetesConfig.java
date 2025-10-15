package com.fz.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class KubernetesConfig {
    @Bean
    public ApiClient apiClient() throws IOException {
        // 自动检测环境（集群内或集群外）
        if (System.getenv("KUBERNETES_SERVICE_HOST") != null) {
            // 在集群内部运行
            return Config.fromCluster();
        } else {
            // 在集群外部运行，使用kubeconfig
            //"C:\Users\19297\.kube\config"
            return Config.fromConfig("C:\\Users\\19297\\.kube\\config");
        }
    }

    @Bean
    public CoreV1Api coreV1Api(ApiClient apiClient) {
        return new CoreV1Api(apiClient);
    }

    @Bean
    public AppsV1Api appsV1Api(ApiClient apiClient) {
        return new AppsV1Api(apiClient);
    }
}
