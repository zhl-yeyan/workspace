Docker 部署 Checklist（发布前核对）

- 镜像
  - [ ] 使用固定 tag（如 1.0.0），不用 latest
  - [ ] 多阶段构建，剔除无关产物
  - [ ] 使用非 root 用户运行
  - [ ] 仅开放必要端口（EXPOSE）

- 配置
  - [ ] 环境变量与密钥未写死在镜像中
  - [ ] 资源限制（--memory/--cpus）按需设置
  - [ ] 健康检查（可选）配置正确
  - [ ] 日志输出到 stdout/stderr，由采集器收集

- 运行/编排
  - [ ] docker run 参数检查：-p/-v/-e/--restart
  - [ ] compose.yaml 服务依赖与卷挂载正确
  - [ ] 时区/DNS 与宿主一致（TZ/DNS）

- 验证
  - [ ] 启动后探活/接口可用
  - [ ] 指标与日志无异常
  - [ ] 回滚策略清晰（镜像 tag、compose 版本回退）


