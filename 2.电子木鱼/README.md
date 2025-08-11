# 电子木鱼 🐟

一个现代化的电子木鱼应用，支持点击敲击、音效播放、功德计数等功能。

## ✨ 功能特色

- 🎵 **真实音效** - 木鱼敲击音效，支持开关控制
- 📊 **功德计数** - 记录敲击次数，本地存储保存
- 🎨 **精美动画** - 点击动画、涟漪效果、视差背景
- 📱 **响应式设计** - 支持桌面端和移动端
- ⌨️ **键盘支持** - 空格键快速敲击
- 🔄 **数据持久化** - 自动保存功德数和设置

## 🚀 快速开始

### 本地运行

1. 克隆或下载项目文件
2. 在项目根目录启动本地服务器：

```bash
# 使用Python
python -m http.server 8000

# 使用Node.js
npx http-server

# 使用PHP
php -S localhost:8000
```

3. 在浏览器中访问 `http://localhost:8000`

### 直接打开

直接双击 `index.html` 文件即可在浏览器中运行（部分功能可能受限）。

## 📁 项目结构

```
电子木鱼/
├── index.html          # 主页面
├── styles.css          # 样式文件
├── script.js           # 功能脚本
├── sounds/             # 音效文件夹
│   ├── README.md       # 音效说明
│   ├── woodfish.mp3    # 木鱼音效（需要添加）
│   └── woodfish.ogg    # 木鱼音效备用格式（需要添加）
└── README.md           # 项目说明
```

## 🎵 音效配置

### 获取音效文件

1. **在线音效库**：
   - [Freesound.org](https://freesound.org/) - 搜索 "wooden fish"
   - [Zapsplat](https://www.zapsplat.com/) - 搜索 "wooden fish sound"

2. **录制真实木鱼**：使用录音设备录制

3. **在线生成器**：[Online Tone Generator](https://www.szynalski.com/tone-generator/)

### 音效要求

- 时长：0.5-1秒
- 格式：MP3 和 OGG
- 音质：44.1kHz, 16bit
- 音量：适中

### 备用方案

如果没有音效文件，应用会自动使用 Web Audio API 生成简化的木鱼音效。

## 🌐 部署方案

### 1. 静态网站托管

#### GitHub Pages
```bash
# 创建GitHub仓库
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/yourusername/woodfish.git
git push -u origin main

# 在GitHub设置中启用Pages
```

#### Netlify
1. 注册 [Netlify](https://netlify.com)
2. 拖拽项目文件夹到Netlify部署区域
3. 自动部署完成

#### Vercel
```bash
# 安装Vercel CLI
npm i -g vercel

# 部署
vercel
```

### 2. 云服务器部署

#### Nginx配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/woodfish;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # 缓存静态资源
    location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg|mp3|ogg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

#### Apache配置
```apache
<VirtualHost *:80>
    ServerName your-domain.com
    DocumentRoot /var/www/woodfish
    
    <Directory /var/www/woodfish>
        AllowOverride All
        Require all granted
    </Directory>
</VirtualHost>
```

### 3. CDN加速

#### 使用Cloudflare
1. 注册 [Cloudflare](https://cloudflare.com)
2. 添加域名
3. 更新DNS记录
4. 启用CDN和缓存

#### 使用阿里云CDN
1. 在阿里云控制台创建CDN加速域名
2. 配置源站信息
3. 设置缓存规则

### 4. Docker部署

#### Dockerfile
```dockerfile
FROM nginx:alpine
COPY . /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### 构建和运行
```bash
# 构建镜像
docker build -t woodfish .

# 运行容器
docker run -d -p 80:80 woodfish
```

### 5. 小程序部署

#### 微信小程序
1. 创建小程序项目
2. 将HTML转换为WXML
3. 将CSS转换为WXSS
4. 将JavaScript适配小程序API

#### 支付宝小程序
1. 使用支付宝小程序开发工具
2. 适配支付宝小程序框架

## 🔧 自定义配置

### 修改样式
编辑 `styles.css` 文件：
- 修改颜色主题
- 调整动画效果
- 更改布局样式

### 修改功能
编辑 `script.js` 文件：
- 调整音效参数
- 修改动画时长
- 添加新功能

### 修改文本
编辑 `index.html` 文件：
- 更改标题和描述
- 修改按钮文本
- 调整页面结构

## 📱 移动端优化

- 响应式设计，适配各种屏幕尺寸
- 触摸事件支持
- 移动端音效优化
- PWA支持（可添加）

## 🔒 隐私说明

- 所有数据存储在本地浏览器中
- 不收集用户个人信息
- 不向外部服务器发送数据

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进项目！

## 📄 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

感谢所有为这个项目提供灵感和帮助的朋友们！

---

**愿以此功德，庄严佛净土** 🙏 