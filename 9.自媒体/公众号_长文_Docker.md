Docker 入门到实战（面向 Java 工程师）

发布日期：2025-08-11  作者：你的名称  标签：Docker｜Java｜DevOps｜上线部署

[TOC]

![封面占位](assets/docker/cover.png)

### 一、什么是 Docker，为什么用
- 进程级隔离，比虚拟机更轻、更快、更易于迁移。
- 解决“环境不一致、交付慢、依赖复杂”的痛点。
- 关键收益：一套模板，从本地到服务器一致运行；镜像可回滚、可复用、易自动化。

### 二、核心概念速览（30 秒搞懂）
- 镜像 Image：只读模板，包含运行环境与应用层。
- 容器 Container：镜像的运行实例，可启动/停止/复制。
- 仓库 Registry：存放镜像的地方（Docker Hub/企业私有仓）。
- Dockerfile：制作镜像的“配方”。

![架构示意图占位](assets/docker/arch.png)

### 三、一步步上手（附可复制模板）

#### 3.1 最小 Dockerfile（Java 运行环境）
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY app.jar app.jar
USER 1000
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
```

#### 3.2 多阶段构建（体积更小，层次更干净）
```dockerfile
# 构建阶段
FROM eclipse-temurin:17-jdk AS build
WORKDIR /src
COPY . .
RUN ./mvnw -DskipTests package

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/app.jar app.jar
USER 1000
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

#### 3.3 .dockerignore（加速与瘦身）
```text
.git
target
node_modules
*.log
*.iml
.vscode
.idea
```

#### 3.4 运行容器（常用参数）
```bash
docker build -t app:1.0.0 .
docker run -d --name app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -v ./logs:/logs \
  --restart=always app:1.0.0
```
- -p 端口映射；-v 卷挂载；-e 环境变量；--restart 重启策略；--memory/--cpus 资源限制。

#### 3.5 docker compose 一键起依赖（后端+MySQL+Redis）
```yaml
services:
  app:
    image: app:1.0.0
    ports: ["8080:8080"]
    environment:
      SPRING_PROFILES_ACTIVE: prod
      TZ: Asia/Shanghai
    depends_on: [db, redis]
    restart: always

  db:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: app
      TZ: Asia/Shanghai
    volumes: ["dbdata:/var/lib/mysql"]
    restart: always

  redis:
    image: redis:7
    command: ["redis-server", "--appendonly", "yes"]
    restart: always

volumes:
  dbdata:
```

### 四、生产实践与最佳实践清单
- 使用固定 tag，不用 latest；版本可追溯可回滚。
- 多阶段构建，剔除构建产物，减少镜像体积与攻击面。
- 非 root 运行；仅开放必要端口；合理的文件与目录权限。
- 健康检查（可选）：
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1
```
- 日志输出到 stdout/stderr，由采集器（如 Filebeat/Fluentd）收集。
- 明确资源限制（CPU/内存）；设置 --restart 策略。

### 五、常见坑与排查
- 胖镜像：未使用多阶段/拷贝了无关文件 → 启用多阶段并检查 .dockerignore。
- 权限与安全：以 root 运行 → 使用非 root 用户（USER 1000）。
- latest 引发回滚困难 → 固定 tag（如 1.0.0），并记录 CHANGELOG。
- 日志写文件 → 改为 stdout/stderr；配置日志轮转由宿主处理。
- 时区/DNS 不一致 → 显式设置 TZ/DNS，或在 compose 中统一。

### 六、发布前 Checklist（勾选即过）
- [ ] 使用固定 tag
- [ ] 多阶段构建已启用
- [ ] 非 root 运行
- [ ] 健康检查配置（可选）
- [ ] 资源限制与重启策略
- [ ] 日志输出规范
- [ ] 回滚方案明确（镜像 tag/compose 版本）

### 七、FAQ
- Q：一定要用多阶段吗？
  - A：强烈建议。可显著减少体积、加速拉取、降低攻击面。
- Q：日志怎么采集？
  - A：打到 stdout/stderr，由采集器拉取；或 Sidecar/Agent 方案。
- Q：镜像如何安全？
  - A：减少层；定期扫描（Trivy/Grype）；限制用户与权限。

### 八、获取模板与资料包
- 本文所有模板已打包：Dockerfile.minimal、Dockerfile.multistage、compose.yaml、.dockerignore、部署 Checklist。
- 获取方式：关注公众号【你的名称】回复【Docker】自动获取；或在置顶评论短链领取。

— 完 —


