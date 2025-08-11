// 电子木鱼应用主逻辑
class WoodfishApp {
    constructor() {
        this.meritCount = 0;
        this.soundEnabled = true;
        this.audioContext = null;
        this.audioBuffer = null;
        this.saveTimeout = null; // 用于防抖保存
        
        this.init();
    }

    init() {
        this.loadData();
        this.bindEvents();
        this.updateDisplay();
        this.initAudio();
    }

    // 初始化音频上下文
    initAudio() {
        try {
            this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
            this.createWoodfishSound();
        } catch (error) {
            console.log('音频上下文初始化失败:', error);
        }
    }

    // 创建木鱼音效
    createWoodfishSound() {
        if (!this.audioContext) return;

        const sampleRate = this.audioContext.sampleRate;
        const duration = 0.3; // 300ms
        const buffer = this.audioContext.createBuffer(1, sampleRate * duration, sampleRate);
        const channelData = buffer.getChannelData(0);

        // 生成木鱼音效（简化的敲击声）
        for (let i = 0; i < buffer.length; i++) {
            const t = i / sampleRate;
            const frequency = 200 + Math.random() * 100; // 200-300Hz
            const amplitude = Math.exp(-t * 8) * (1 - t / duration); // 指数衰减
            const noise = (Math.random() - 0.5) * 2; // 白噪声
            
            channelData[i] = amplitude * noise * 0.3;
        }

        this.audioBuffer = buffer;
    }

    // 绑定事件监听器
    bindEvents() {
        const woodfish = document.getElementById('woodfish');
        const soundToggle = document.getElementById('soundToggle');
        const resetBtn = document.getElementById('resetBtn');

        // 木鱼点击事件 - 使用mousedown确保快速响应
        woodfish.addEventListener('mousedown', (e) => {
            console.log('点击事件触发:', new Date().getTime());
            this.handleWoodfishClick(e);
        });
        woodfish.addEventListener('touchstart', (e) => {
            e.preventDefault();
            console.log('触摸事件触发:', new Date().getTime());
            this.handleWoodfishClick(e);
        });
        
        // 音效开关
        soundToggle.addEventListener('click', () => this.toggleSound());
        
        // 重置按钮
        resetBtn.addEventListener('click', () => this.resetMerit());

        // 键盘事件支持
        document.addEventListener('keydown', (e) => {
            if (e.code === 'Space' || e.code === 'Enter') {
                e.preventDefault();
                this.handleWoodfishClick();
            }
        });
    }

    // 处理木鱼点击
    handleWoodfishClick(event) {
        // 使用requestAnimationFrame确保事件不被阻塞
        requestAnimationFrame(() => {
            // 立即增加功德，确保每次点击都被计数
            this.meritCount++;
            
            // 立即更新显示
            this.updateDisplay();
            
            // 防抖保存数据（避免频繁保存）
            this.debouncedSave();
            
            // 播放音效（不阻塞计数）
            if (this.soundEnabled) {
                this.playSound();
            }
            
            // 播放动画（使用独立的动画状态管理）
            this.playAnimation(event);
        });
    }

    // 播放音效
    playSound() {
        // 优先使用Web Audio API，响应更快
        if (this.audioContext && this.audioBuffer) {
            this.playWebAudioSound();
        } else {
            // 备用HTML5音频
            const audio = document.getElementById('woodfishSound');
            if (audio && audio.readyState >= 2) {
                audio.currentTime = 0;
                audio.play().catch(error => {
                    console.log('HTML5音效播放失败:', error);
                });
            }
        }
    }

    // 使用Web Audio API播放音效
    playWebAudioSound() {
        if (!this.audioContext || !this.audioBuffer) return;

        try {
            // 恢复音频上下文（如果被暂停）
            if (this.audioContext.state === 'suspended') {
                this.audioContext.resume();
            }

            const source = this.audioContext.createBufferSource();
            source.buffer = this.audioBuffer;
            source.connect(this.audioContext.destination);
            source.start(0);
        } catch (error) {
            console.log('Web Audio API播放失败:', error);
        }
    }

    // 播放动画
    playAnimation(event) {
        const woodfish = document.getElementById('woodfish');
        const meritNumber = document.getElementById('meritCount');
        
        // 木鱼点击动画 - 简化版本，不阻塞点击
        woodfish.classList.add('woodfish-hit');
        setTimeout(() => {
            woodfish.classList.remove('woodfish-hit');
        }, 150); // 缩短动画时间
        
        // 功德数字脉冲动画
        meritNumber.classList.add('merit-pulse');
        setTimeout(() => {
            meritNumber.classList.remove('merit-pulse');
        }, 200); // 缩短动画时间
        
        // 涟漪效果
        if (event) {
            this.createRipple(event);
        }
    }

    // 创建涟漪效果
    createRipple(event) {
        const woodfish = document.getElementById('woodfish');
        const rect = woodfish.getBoundingClientRect();
        
        const ripple = document.createElement('div');
        ripple.className = 'ripple';
        
        // 限制涟漪大小，避免超出视口
        const maxSize = Math.min(rect.width, rect.height, 200);
        const size = Math.min(maxSize, Math.max(rect.width, rect.height));
        const x = event.clientX - rect.left - size / 2;
        const y = event.clientY - rect.top - size / 2;
        
        ripple.style.width = ripple.style.height = size + 'px';
        ripple.style.left = x + 'px';
        ripple.style.top = y + 'px';
        
        woodfish.appendChild(ripple);
        
        setTimeout(() => {
            if (ripple.parentNode) {
                ripple.remove();
            }
        }, 600);
    }

    // 切换音效
    toggleSound() {
        this.soundEnabled = !this.soundEnabled;
        this.updateSoundButton();
        this.saveData();
    }

    // 更新音效按钮显示
    updateSoundButton() {
        const soundToggle = document.getElementById('soundToggle');
        const btnIcon = soundToggle.querySelector('.btn-icon');
        const btnText = soundToggle.querySelector('.btn-text');
        
        if (this.soundEnabled) {
            btnIcon.textContent = '🔊';
            btnText.textContent = '音效开';
        } else {
            btnIcon.textContent = '🔇';
            btnText.textContent = '音效关';
        }
    }

    // 重置功德
    resetMerit() {
        if (confirm('确定要重置功德数吗？')) {
            this.meritCount = 0;
            this.updateDisplay();
            this.saveData(); // 重置时立即保存
            
            // 重置动画
            const meritNumber = document.getElementById('meritCount');
            meritNumber.classList.add('merit-pulse');
            setTimeout(() => {
                meritNumber.classList.remove('merit-pulse');
            }, 300);
        }
    }

    // 更新显示
    updateDisplay() {
        const meritNumber = document.getElementById('meritCount');
        meritNumber.textContent = this.meritCount.toLocaleString();
    }

    // 防抖保存数据
    debouncedSave() {
        if (this.saveTimeout) {
            clearTimeout(this.saveTimeout);
        }
        this.saveTimeout = setTimeout(() => {
            this.saveData();
        }, 100); // 100ms防抖
    }

    // 保存数据到本地存储
    saveData() {
        const data = {
            meritCount: this.meritCount,
            soundEnabled: this.soundEnabled,
            lastSaved: new Date().toISOString()
        };
        
        try {
            localStorage.setItem('woodfishData', JSON.stringify(data));
        } catch (error) {
            console.log('保存数据失败:', error);
        }
    }

    // 从本地存储加载数据
    loadData() {
        try {
            const savedData = localStorage.getItem('woodfishData');
            if (savedData) {
                const data = JSON.parse(savedData);
                this.meritCount = data.meritCount || 0;
                this.soundEnabled = data.soundEnabled !== undefined ? data.soundEnabled : true;
            }
        } catch (error) {
            console.log('加载数据失败:', error);
            this.meritCount = 0;
            this.soundEnabled = true;
        }
    }

    // 获取统计信息
    getStats() {
        return {
            meritCount: this.meritCount,
            soundEnabled: this.soundEnabled,
            lastSaved: new Date().toISOString()
        };
    }

    // 测试快速点击功能
    testRapidClick() {
        console.log('开始快速点击测试...');
        const startCount = this.meritCount;
        const startTime = Date.now();
        let clickCount = 0;
        
        // 模拟10次快速点击
        for (let i = 0; i < 10; i++) {
            setTimeout(() => {
                clickCount++;
                console.log(`第${clickCount}次点击`);
                this.handleWoodfishClick();
            }, i * 30); // 每30ms一次点击，更快
        }
        
        // 检查结果
        setTimeout(() => {
            const endCount = this.meritCount;
            const endTime = Date.now();
            const actualClicks = endCount - startCount;
            
            console.log(`快速点击测试结果:`);
            console.log(`- 预期点击次数: 10`);
            console.log(`- 实际点击次数: ${actualClicks}`);
            console.log(`- 测试耗时: ${endTime - startTime}ms`);
            console.log(`- 准确性: ${actualClicks === 10 ? '✅ 100%' : '❌ ' + (actualClicks/10*100) + '%'}`);
            
            if (actualClicks !== 10) {
                console.log('⚠️ 检测到点击丢失，可能的原因:');
                console.log('- 动画阻塞了点击事件');
                console.log('- 浏览器事件处理限制');
                console.log('- CSS样式干扰');
            }
        }, 500);
    }
}

// 页面加载完成后初始化应用
document.addEventListener('DOMContentLoaded', () => {
    // 创建应用实例
    window.woodfishApp = new WoodfishApp();
    
    // 预加载音效
    const audio = document.getElementById('woodfishSound');
    if (audio) {
        audio.load();
    }
    
    // 添加页面可见性变化监听（暂停/恢复音效）
    document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
            audio.pause();
        }
    });
    
    console.log('电子木鱼应用已启动 🐟');
    
    // 添加快速点击测试功能（开发模式）
    if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
        console.log('开发模式：可以在控制台使用 woodfishApp.testRapidClick() 测试快速点击功能');
    }
});

// 添加一些额外的交互效果
document.addEventListener('DOMContentLoaded', () => {
    // 添加鼠标移动视差效果
    const container = document.querySelector('.container');
    
    document.addEventListener('mousemove', (e) => {
        const { clientX, clientY } = e;
        const { innerWidth, innerHeight } = window;
        
        const x = (clientX - innerWidth / 2) / innerWidth;
        const y = (clientY - innerHeight / 2) / innerHeight;
        
        container.style.transform = `translate(${x * 10}px, ${y * 10}px)`;
    });
    
    // 添加键盘快捷键提示
    const hint = document.querySelector('.click-hint');
    if (hint) {
        hint.textContent = '点击木鱼 或 按空格键';
    }
}); 