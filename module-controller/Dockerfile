# 基础镜像
FROM openjdk:8

# 维护者信息
LABEL maintainer="your-email@example.com"

# 设置工作目录
WORKDIR /app

# 复制构建好的 jar 文件
COPY target/module-controller-*.jar app.jar

# 暴露端口
EXPOSE 8083

# 设置 JVM 参数
ENV JAVA_OPTS=""

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]