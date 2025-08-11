// 电子木鱼小程序页面逻辑
Page({
  data: {
    meritCount: 0,
    soundEnabled: true,
    woodfishHit: false,
    meritPulse: false,
    innerAudioContext: null
  },

  onLoad() {
    // 页面加载时初始化
    this.loadData();
    this.initAudio();
  },

  onShow() {
    // 页面显示时刷新数据
    this.loadData();
  },

  // 初始化音频
  initAudio() {
    this.setData({
      innerAudioContext: wx.createInnerAudioContext()
    });
    
    const audioContext = this.data.innerAudioContext;
    audioContext.src = '/sounds/woodfish.mp3'; // 需要添加音效文件
    audioContext.loop = false;
    
    // 音频播放错误处理
    audioContext.onError((res) => {
      console.log('音频播放失败:', res);
      // 如果音效文件不存在，使用震动反馈
      this.playVibration();
    });
  },

  // 处理木鱼点击
  handleWoodfishClick() {
    // 增加功德数
    const newCount = this.data.meritCount + 1;
    this.setData({
      meritCount: newCount,
      woodfishHit: true,
      meritPulse: true
    });

    // 播放音效
    if (this.data.soundEnabled) {
      this.playSound();
    }

    // 播放震动反馈
    this.playVibration();

    // 保存数据
    this.saveData();

    // 重置动画状态
    setTimeout(() => {
      this.setData({
        woodfishHit: false,
        meritPulse: false
      });
    }, 200);
  },

  // 播放音效
  playSound() {
    const audioContext = this.data.innerAudioContext;
    if (audioContext) {
      audioContext.stop();
      audioContext.play();
    }
  },

  // 播放震动反馈
  playVibration() {
    wx.vibrateShort({
      type: 'light'
    });
  },

  // 切换音效开关
  toggleSound() {
    const newSoundEnabled = !this.data.soundEnabled;
    this.setData({
      soundEnabled: newSoundEnabled
    });
    this.saveData();
  },

  // 重置功德数
  resetMerit() {
    wx.showModal({
      title: '确认重置',
      content: '确定要重置功德数吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({
            meritCount: 0
          });
          this.saveData();
          wx.showToast({
            title: '已重置',
            icon: 'success'
          });
        }
      }
    });
  },

  // 保存数据到本地存储
  saveData() {
    try {
      wx.setStorageSync('woodfish_merit_count', this.data.meritCount);
      wx.setStorageSync('woodfish_sound_enabled', this.data.soundEnabled);
    } catch (error) {
      console.log('保存数据失败:', error);
    }
  },

  // 从本地存储加载数据
  loadData() {
    try {
      const meritCount = wx.getStorageSync('woodfish_merit_count') || 0;
      const soundEnabled = wx.getStorageSync('woodfish_sound_enabled');
      
      this.setData({
        meritCount: meritCount,
        soundEnabled: soundEnabled !== false // 默认开启音效
      });
    } catch (error) {
      console.log('加载数据失败:', error);
    }
  },

  // 页面卸载时清理资源
  onUnload() {
    if (this.data.innerAudioContext) {
      this.data.innerAudioContext.destroy();
    }
  }
}); 