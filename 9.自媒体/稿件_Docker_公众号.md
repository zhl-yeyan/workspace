Docker 入门到实战（面向 Java 工程师）｜公众号长文稿

一、你将收获什么
- 搭建生产可用的 Java 镜像（多阶段构建）
- 一键启动后端+Redis+MySQL（compose 模板）
- 部署 Checklist 与常见坑排查

二、什么是 Docker（与虚拟机对比）
- 进程级隔离 vs 系统级虚拟；资源占用、启动时间、迁移成本对比
- 核心概念：镜像/容器/仓库/Dockerfile；分层与缓存

三、Dockerfile：从最小到生产
1) 最小可用
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY app.jar app.jar
USER 1000
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
```
2) 多阶段构建（体积更小）
```dockerfile
FROM eclipse-temurin:17-jdk AS build
WORKDIR /src
COPY . .
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/app.jar app.jar
USER 1000
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```
3) 生产最佳实践
- 固定 tag，不用 latest；非 root；只开放必要端口
- 合理健康检查；利用层缓存；忽略无关文件（.dockerignore）

四、运行与参数
```bash
docker run -d --name app -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod -v ./logs:/logs --restart=always app:1.0.0
```
- 常用：-p 端口映射、-v 卷挂载、-e 环境变量、--restart 重启策略、--memory/--cpus 资源限制

五、compose 一键启动依赖
```yaml
services:
  app:
    image: app:1.0.0
    ports: ["8080:8080"]
    environment:
      SPRING_PROFILES_ACTIVE: prod
    depends_on: [db, redis]
  db:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: root
    volumes: ["dbdata:/var/lib/mysql"]
  redis:
    image: redis:7
volumes:
  dbdata:
```

六、常见坑与排查
- 胖镜像：未多阶段、未清理构建产物 → 使用 jdk 构建、jre 运行
- 权限与安全：root 运行 → USER 1000、最小权限
- 日志：写文件难采集 → 打到 stdout/stderr，由采集器收集
- 时区/DNS：容器与宿主不一致 → 显式设置 TZ、DNS

七、Checklist（发布前核对）
- [ ] 使用固定 tag；[ ] 多阶段构建；[ ] 非 root；[ ] 健康检查；[ ] 资源限制；[ ] 日志输出规范

八、获取模板
- 完整 Dockerfile/compose/部署清单已打包，关注公众号【自定义名称】回复【Docker】自动获取。


