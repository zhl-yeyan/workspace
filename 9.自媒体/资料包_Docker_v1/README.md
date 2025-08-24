Docker 资料包 v1（Java 项目）

包含内容
- Dockerfile.minimal：最小可用 Java 运行镜像
- Dockerfile.multistage：多阶段构建（JDK 构建，JRE 运行），体积更小
- compose.yaml：一键启动 后端 + MySQL + Redis
- .dockerignore：忽略构建无关文件，加速与瘦身
- 部署Checklist.md：发布前核对项与常见坑

使用方法
1) 将 Dockerfile 置于你的 Spring Boot 工程根目录；若使用多阶段，请确保存在可构建的 Jar（或使用 maven wrapper）。
2) 运行：
   - 构建：docker build -t app:1.0.0 -f Dockerfile .
   - 运行：docker run -d --name app -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod --restart=always app:1.0.0
3) 如需整套依赖，修改 compose.yaml 的镜像名或 build 配置，执行：docker compose up -d

注意
- 固定 tag，不用 latest；非 root 运行；日志打到 stdout/stderr 交由采集器收集。
- 不要在镜像中保存密钥/凭证，使用环境变量或密钥管理服务。

授权
- 本资料包用于学习与内部演示，可自由分享；请保留出处。


