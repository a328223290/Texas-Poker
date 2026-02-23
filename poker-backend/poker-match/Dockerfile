FROM eclipse-temurin:21-jdk-alpine AS builder

# 安装 bash（gradlew 是 bash 脚本）
RUN apk add --no-cache bash

WORKDIR /workspace

# 先复制 Gradle 相关文件
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

# 复制所有模块源码
COPY poker-common poker-common
COPY poker-match poker-match

# 添加执行权限并构建
RUN chmod +x gradlew && ./gradlew :poker-match:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/poker-match/build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
