Docker 入门到实战｜B站专属稿件（10 分钟）

一、标题备选
- 10分钟入门 Docker：从镜像到 Compose，一条龙搞定
- Java 项目 Docker 化：多阶段构建把镜像体积砍半

二、结构与时间分配（含章节时间戳）
- 00:00 开场与收益：环境一致、一键交付、模板可复用
- 00:20 Docker vs 虚拟机：隔离与资源占用对比
- 01:40 核心概念：镜像/容器/仓库/Dockerfile；分层与缓存
- 03:00 实战一：Dockerfile（多阶段）、非 root、健康检查、JAVA_OPTS
- 06:30 实战二：docker run 参数：-p/-v/-e/--restart/--memory
- 08:30 实战三：docker compose 起后端+Redis+MySQL
- 09:30 总结与踩坑：latest/胖镜像/root/日志/时区；置顶领取模板

三、关键代码片段
- 健康检查（可选）
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1
```
- 运行示例
```bash
docker run -d --name app -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod -v ./logs:/logs --restart=always app:1.0.0
```
- compose 示例
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

四、上架与包装
- 封面：痛点词+收益词+关键词（例：压测不过？→一键上线→Docker/Compose）
- 简介：放源码/模板/清单 + 合集导航；置顶评论重复承接链接。
- 标签：Java/后端/运维/Docker/Spring Boot/DevOps/Kubernetes。

五、置顶评论
- 完整 Dockerfile 与 compose 模板、部署 Checklist 已打包，点击置顶链接领取；合集：Java 项目上线全流程。


