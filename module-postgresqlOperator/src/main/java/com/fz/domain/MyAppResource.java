package com.fz.domain;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import io.fabric8.kubernetes.model.annotation.Kind;

// 定义自定义资源
@Group("yourdomain.com")
@Version("v1")
@Kind("MyApp")
public class MyAppResource extends CustomResource<MyAppSpec, MyAppStatus> {
    // 自定义资源类
}

