@echo off
echo 正在启动电子木鱼应用...
echo.

REM 检查Python是否安装
python --version >nul 2>&1
if %errorlevel% == 0 (
    echo 使用Python启动服务器...
    python -m http.server 8000
    goto :end
)

REM 检查Node.js是否安装
node --version >nul 2>&1
if %errorlevel% == 0 (
    echo 使用Node.js启动服务器...
    npx http-server -p 8000 -o
    goto :end
)

REM 检查PHP是否安装
php --version >nul 2>&1
if %errorlevel% == 0 (
    echo 使用PHP启动服务器...
    php -S localhost:8000
    goto :end
)

echo 错误：未找到Python、Node.js或PHP
echo 请安装其中任意一个来启动本地服务器
echo.
echo 或者直接双击 index.html 文件在浏览器中打开
pause

:end 