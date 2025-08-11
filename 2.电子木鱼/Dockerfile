# 使用官方Nginx镜像作为基础镜像
FROM nginx:alpine

# 复制项目文件到Nginx默认目录
COPY . /usr/share/nginx/html/

# 复制自定义Nginx配置（可选）
# COPY nginx.conf /etc/nginx/nginx.conf

# 暴露80端口
EXPOSE 80

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"] 