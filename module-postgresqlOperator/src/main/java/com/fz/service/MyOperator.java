package com.fz.service;


import com.fz.domain.MyAppResource;
import io.javaoperatorsdk.operator.api.Context;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.UpdateControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;


public class MyOperator implements ResourceController<MyAppResource> {
    private final KubernetesClient client;
    private final Logger log = LoggerFactory.getLogger(MyOperator.class);

    public MyOperator(KubernetesClient client) {
        log.info("MyOperator初始化");
        this.client = client;
    }

    @Override
    public boolean deleteResource(MyAppResource myAppResource, Context<MyAppResource> context) {
        log.info("删除资源: {}", myAppResource.getMetadata().getName());
        return false;
    }

    @Override
    public UpdateControl<MyAppResource> createOrUpdateResource(MyAppResource myAppResource, Context<MyAppResource> context) {
        log.info("创建或更新资源: {}", myAppResource.getMetadata().getName());
        return UpdateControl.updateCustomResource(myAppResource);
    }

}

