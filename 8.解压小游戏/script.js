// 全局变量
let currentGame = 'bubble';
let audioContext = null;
let currentAudio = null;

// 初始化
document.addEventListener('DOMContentLoaded', function() {
    initializeNavigation();
    initializeBubbleGame();
    initializeSandGame();
    initializeLiquidGame();
    initializeParticleGame();
    initializeSoundGame();
    
    // 初始化音频上下文
    try {
        audioContext = new (window.AudioContext || window.webkitAudioContext)();
    } catch (e) {
        console.log('Web Audio API not supported');
    }
});

// 导航功能
function initializeNavigation() {
    const navBtns = document.querySelectorAll('.nav-btn');
    const gamePanels = document.querySelectorAll('.game-panel');
    
    navBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const gameType = btn.dataset.game;
            
            // 更新按钮状态
            navBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            
            // 更新面板显示
            gamePanels.forEach(panel => {
                panel.classList.remove('active');
                if (panel.id === gameType) {
                    panel.classList.add('active');
                }
            });
            
            currentGame = gameType;
        });
    });
}

// 泡泡纸游戏
function initializeBubbleGame() {
    const bubbleGrid = document.querySelector('.bubble-grid');
    const resetBtn = document.getElementById('reset-bubbles');
    const autoPopBtn = document.getElementById('auto-pop');
    
    // 创建泡泡网格
    function createBubbles() {
        bubbleGrid.innerHTML = '';
        const rows = 8;
        const cols = 12;
        
        for (let i = 0; i < rows * cols; i++) {
            const bubble = document.createElement('div');
            bubble.className = 'bubble';
            bubble.addEventListener('click', popBubble);
            bubbleGrid.appendChild(bubble);
        }
    }
    
    // 爆破泡泡
    function popBubble(e) {
        const bubble = e.target;
        if (!bubble.classList.contains('popped')) {
            bubble.classList.add('popped');
            playBubbleSound();
            
            // 随机延迟后重置泡泡
            setTimeout(() => {
                bubble.classList.remove('popped');
            }, 1000 + Math.random() * 2000);
        }
    }
    
    // 播放泡泡音效
    function playBubbleSound() {
        if (audioContext) {
            const oscillator = audioContext.createOscillator();
            const gainNode = audioContext.createGain();
            
            oscillator.connect(gainNode);
            gainNode.connect(audioContext.destination);
            
            oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
            oscillator.frequency.exponentialRampToValueAtTime(200, audioContext.currentTime + 0.1);
            
            gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
            gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.1);
            
            oscillator.start(audioContext.currentTime);
            oscillator.stop(audioContext.currentTime + 0.1);
        }
    }
    
    // 自动爆破
    function autoPop() {
        const bubbles = document.querySelectorAll('.bubble:not(.popped)');
        if (bubbles.length > 0) {
            const randomBubble = bubbles[Math.floor(Math.random() * bubbles.length)];
            randomBubble.click();
            setTimeout(autoPop, 100 + Math.random() * 200);
        }
    }
    
    resetBtn.addEventListener('click', createBubbles);
    autoPopBtn.addEventListener('click', autoPop);
    
    // 初始化泡泡
    createBubbles();
}

// 沙子画游戏
function initializeSandGame() {
    const canvas = document.getElementById('sand-canvas');
    const ctx = canvas.getContext('2d');
    const clearBtn = document.getElementById('clear-sand');
    const rainBtn = document.getElementById('rain-sand');
    const colorPicker = document.getElementById('sand-color');
    
    let isDrawing = false;
    let sandColor = '#f4d03f';
    
    // 设置画布
    canvas.width = 800;
    canvas.height = 600;
    
    // 鼠标事件
    canvas.addEventListener('mousedown', startDrawing);
    canvas.addEventListener('mousemove', draw);
    canvas.addEventListener('mouseup', stopDrawing);
    canvas.addEventListener('mouseleave', stopDrawing);
    
    // 触摸事件（移动端）
    canvas.addEventListener('touchstart', handleTouch);
    canvas.addEventListener('touchmove', handleTouch);
    canvas.addEventListener('touchend', stopDrawing);
    
    function startDrawing(e) {
        isDrawing = true;
        draw(e);
    }
    
    function draw(e) {
        if (!isDrawing) return;
        
        const rect = canvas.getBoundingClientRect();
        const x = (e.clientX || e.touches[0].clientX) - rect.left;
        const y = (e.clientY || e.touches[0].clientY) - rect.top;
        
        ctx.fillStyle = sandColor;
        ctx.beginPath();
        ctx.arc(x, y, 3, 0, Math.PI * 2);
        ctx.fill();
        
        // 添加一些随机粒子
        for (let i = 0; i < 3; i++) {
            const offsetX = x + (Math.random() - 0.5) * 10;
            const offsetY = y + (Math.random() - 0.5) * 10;
            ctx.beginPath();
            ctx.arc(offsetX, offsetY, 1, 0, Math.PI * 2);
            ctx.fill();
        }
    }
    
    function stopDrawing() {
        isDrawing = false;
    }
    
    function handleTouch(e) {
        e.preventDefault();
        if (e.type === 'touchstart') {
            startDrawing(e);
        } else if (e.type === 'touchmove') {
            draw(e);
        }
    }
    
    // 清空画布
    clearBtn.addEventListener('click', () => {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    });
    
    // 沙子雨
    rainBtn.addEventListener('click', () => {
        for (let i = 0; i < 50; i++) {
            setTimeout(() => {
                const x = Math.random() * canvas.width;
                const y = Math.random() * canvas.height;
                ctx.fillStyle = sandColor;
                ctx.beginPath();
                ctx.arc(x, y, 2, 0, Math.PI * 2);
                ctx.fill();
            }, i * 50);
        }
    });
    
    // 颜色选择
    colorPicker.addEventListener('change', (e) => {
        sandColor = e.target.value;
    });
}

// 液体流游戏
function initializeLiquidGame() {
    const canvas = document.getElementById('liquid-canvas');
    const ctx = canvas.getContext('2d');
    const clearBtn = document.getElementById('clear-liquid');
    const addBtn = document.getElementById('add-liquid');
    const colorPicker = document.getElementById('liquid-color');
    
    let liquidColor = '#3498db';
    let drops = [];
    
    // 设置画布
    canvas.width = 800;
    canvas.height = 600;
    
    // 液体滴类
    class LiquidDrop {
        constructor(x, y, color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = Math.random() * 20 + 10;
            this.speed = Math.random() * 2 + 1;
            this.opacity = 1;
        }
        
        update() {
            this.y += this.speed;
            this.opacity -= 0.005;
            return this.opacity > 0;
        }
        
        draw() {
            ctx.save();
            ctx.globalAlpha = this.opacity;
            ctx.fillStyle = this.color;
            ctx.beginPath();
            ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
            ctx.fill();
            ctx.restore();
        }
    }
    
    // 动画循环
    function animate() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        
        drops = drops.filter(drop => {
            drop.update();
            drop.draw();
            return drop.opacity > 0;
        });
        
        requestAnimationFrame(animate);
    }
    
    // 添加液体滴
    function addLiquidDrop(x, y) {
        drops.push(new LiquidDrop(x, y, liquidColor));
    }
    
    // 鼠标点击添加液体
    canvas.addEventListener('click', (e) => {
        const rect = canvas.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        addLiquidDrop(x, y);
    });
    
    // 清空液体
    clearBtn.addEventListener('click', () => {
        drops = [];
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    });
    
    // 添加液体按钮
    addBtn.addEventListener('click', () => {
        for (let i = 0; i < 10; i++) {
            setTimeout(() => {
                const x = Math.random() * canvas.width;
                const y = Math.random() * canvas.height;
                addLiquidDrop(x, y);
            }, i * 100);
        }
    });
    
    // 颜色选择
    colorPicker.addEventListener('change', (e) => {
        liquidColor = e.target.value;
    });
    
    // 开始动画
    animate();
}

// 粒子爆炸游戏
function initializeParticleGame() {
    const canvas = document.getElementById('particle-canvas');
    const ctx = canvas.getContext('2d');
    const clearBtn = document.getElementById('clear-particles');
    const fireworksBtn = document.getElementById('fireworks');
    const countSlider = document.getElementById('particle-count');
    
    let particles = [];
    let particleCount = 30;
    
    // 设置画布
    canvas.width = 800;
    canvas.height = 600;
    
    // 粒子类
    class Particle {
        constructor(x, y, color) {
            this.x = x;
            this.y = y;
            this.vx = (Math.random() - 0.5) * 10;
            this.vy = (Math.random() - 0.5) * 10;
            this.color = color;
            this.size = Math.random() * 4 + 2;
            this.life = 1;
            this.decay = Math.random() * 0.02 + 0.01;
        }
        
        update() {
            this.x += this.vx;
            this.y += this.vy;
            this.vy += 0.1; // 重力
            this.life -= this.decay;
            return this.life > 0;
        }
        
        draw() {
            ctx.save();
            ctx.globalAlpha = this.life;
            ctx.fillStyle = this.color;
            ctx.beginPath();
            ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
            ctx.fill();
            ctx.restore();
        }
    }
    
    // 创建爆炸效果
    function createExplosion(x, y, color) {
        for (let i = 0; i < particleCount; i++) {
            particles.push(new Particle(x, y, color));
        }
    }
    
    // 动画循环
    function animate() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        
        particles = particles.filter(particle => {
            particle.update();
            particle.draw();
            return particle.life > 0;
        });
        
        requestAnimationFrame(animate);
    }
    
    // 鼠标点击创建爆炸
    canvas.addEventListener('click', (e) => {
        const rect = canvas.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57'];
        const color = colors[Math.floor(Math.random() * colors.length)];
        createExplosion(x, y, color);
    });
    
    // 清空粒子
    clearBtn.addEventListener('click', () => {
        particles = [];
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    });
    
    // 烟花模式
    fireworksBtn.addEventListener('click', () => {
        for (let i = 0; i < 5; i++) {
            setTimeout(() => {
                const x = Math.random() * canvas.width;
                const y = Math.random() * canvas.height;
                const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57'];
                const color = colors[Math.floor(Math.random() * colors.length)];
                createExplosion(x, y, color);
            }, i * 200);
        }
    });
    
    // 粒子数量滑块
    countSlider.addEventListener('input', (e) => {
        particleCount = parseInt(e.target.value);
    });
    
    // 开始动画
    animate();
}

// 解压音效游戏
function initializeSoundGame() {
    const soundBtns = document.querySelectorAll('.sound-btn');
    const volumeSlider = document.getElementById('volume');
    
    // 音效数据
    const sounds = {
        bubble: { frequency: 800, duration: 0.1, type: 'sine' },
        rain: { frequency: 200, duration: 0.5, type: 'noise' },
        waves: { frequency: 150, duration: 1, type: 'sine' },
        fire: { frequency: 100, duration: 0.3, type: 'sawtooth' },
        wind: { frequency: 300, duration: 0.8, type: 'triangle' },
        forest: { frequency: 400, duration: 1.2, type: 'sine' },
        cafe: { frequency: 250, duration: 0.6, type: 'square' },
        whitenoise: { frequency: 0, duration: 2, type: 'noise' }
    };
    
    // 播放音效
    function playSound(soundType) {
        if (!audioContext) return;
        
        const sound = sounds[soundType];
        const volume = parseFloat(volumeSlider.value);
        
        if (sound.type === 'noise') {
            // 白噪音
            const bufferSize = audioContext.sampleRate * sound.duration;
            const buffer = audioContext.createBuffer(1, bufferSize, audioContext.sampleRate);
            const output = buffer.getChannelData(0);
            
            for (let i = 0; i < bufferSize; i++) {
                output[i] = Math.random() * 2 - 1;
            }
            
            const source = audioContext.createBufferSource();
            const gainNode = audioContext.createGain();
            
            source.buffer = buffer;
            source.connect(gainNode);
            gainNode.connect(audioContext.destination);
            
            gainNode.gain.setValueAtTime(volume * 0.3, audioContext.currentTime);
            gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + sound.duration);
            
            source.start();
            source.stop(audioContext.currentTime + sound.duration);
        } else {
            // 其他音效
            const oscillator = audioContext.createOscillator();
            const gainNode = audioContext.createGain();
            
            oscillator.connect(gainNode);
            gainNode.connect(audioContext.destination);
            
            oscillator.type = sound.type;
            oscillator.frequency.setValueAtTime(sound.frequency, audioContext.currentTime);
            
            gainNode.gain.setValueAtTime(volume * 0.3, audioContext.currentTime);
            gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + sound.duration);
            
            oscillator.start(audioContext.currentTime);
            oscillator.stop(audioContext.currentTime + sound.duration);
        }
    }
    
    // 按钮点击事件
    soundBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const soundType = btn.dataset.sound;
            
            // 添加播放动画
            btn.classList.add('playing');
            setTimeout(() => {
                btn.classList.remove('playing');
            }, 1000);
            
            playSound(soundType);
        });
    });
    
    // 音量控制
    volumeSlider.addEventListener('input', (e) => {
        // 音量变化时的反馈
        const volume = parseFloat(e.target.value);
        if (volume > 0.5) {
            playSound('bubble');
        }
    });
}

// 工具函数：生成随机颜色
function getRandomColor() {
    const colors = [
        '#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57',
        '#ff9ff3', '#54a0ff', '#5f27cd', '#00d2d3', '#ff9f43'
    ];
    return colors[Math.floor(Math.random() * colors.length)];
}

// 工具函数：生成随机数
function random(min, max) {
    return Math.random() * (max - min) + min;
}

// 工具函数：限制数值范围
function clamp(value, min, max) {
    return Math.min(Math.max(value, min), max);
}