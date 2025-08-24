<template>
  <div class="page">
    <header class="header">
      <div class="title">新对话</div>
      <button class="pair-btn">对对</button>
    </header>

    <main class="chat">
      <div v-if="messages.length === 0" class="empty-tip"></div>
      <div
        v-for="(m, idx) in messages"
        :key="idx"
        class="message"
        :class="{ me: m.role === 'user' }"
      >
        <div class="bubble">{{ m.content }}</div>
      </div>
    </main>

    <footer class="composer">
      <div class="input-wrap">
        <input
          v-model="input"
          class="text-input"
          placeholder="给 DeepSeek 发送消息"
          @keydown.enter.prevent="onSendClick"
        />
        <div class="actions">
          <button
            class="action"
            :class="{ active: enableDeepThink }"
            @click="enableDeepThink = !enableDeepThink"
            :aria-pressed="enableDeepThink.toString()"
          >深度思考</button>
          <button
            class="action"
            :class="{ active: enableWebSearch }"
            @click="enableWebSearch = !enableWebSearch"
            :aria-pressed="enableWebSearch.toString()"
          >联网搜索</button>
          <button class="action" @click="toggleSse">SSEServer</button>
          <button class="send" @click="onSendClick">
            <span v-if="!showSquare">→</span>
            <span v-else>■</span>
          </button>
        </div>
      </div>
    </footer>
  </div>
  
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { connectSse, closeSse, isSseConnected, setSseHandler } from './services/sse';

const input = ref('');
const showSquare = ref(false);
const enableDeepThink = ref(false);
const enableWebSearch = ref(false);
type ChatMessage = { role: 'user' | 'assistant'; content: string };
const messages = ref<ChatMessage[]>([]);
const streamingAssistantIndex = ref<number | null>(null);

async function send() {
  const text = input.value.trim();
  if (!text) return;
  messages.value.push({ role: 'user', content: text });
  input.value = '';
  const payload = {
    text,
    deepThink: enableDeepThink.value,
    webSearch: enableWebSearch.value,
    timestamp: Date.now(),
  };
  const url = `/api/strem/str?msg=${encodeURIComponent(JSON.stringify(payload))}`;
  try {
    // 确保已连接 SSE，以接收流式返回
    ensureSseConnected();
    const res = await fetch(url);
    const bodyText = await res.text();
    console.log('GET /api/strem/str response:', bodyText);
    if (bodyText) {
      // 如果没有产生流式气泡，则直接一次性渲染返回文本
      if (streamingAssistantIndex.value == null) {
        messages.value.push({ role: 'assistant', content: String(bodyText) });
      } else {
        // 已有流式气泡，则把最终文本也拼接进去
        const idx = streamingAssistantIndex.value;
        const current = messages.value[idx];
        if (current) {
          current.content += String(bodyText);
          messages.value[idx] = { ...current };
        }
      }
      // 结束本轮
      showSquare.value = false;
      streamingAssistantIndex.value = null;
    }
  } catch (e) {
    console.error('GET /api/strem/str error:', e);
  }
}

function onSendClick() {
  send();
  showSquare.value = true;
}

function toggleSse() {
  if (isSseConnected()) {
    closeSse();
    return;
  }
  connectSse('/api/', onSseChunk);
}

function onSseChunk(data: string) {
  // 把 SSE 片段追加到同一个助手气泡，实现“流式输出”
  if (streamingAssistantIndex.value == null) {
    streamingAssistantIndex.value = messages.value.length;
    messages.value.push({ role: 'assistant', content: '' });
  }
  const idx = streamingAssistantIndex.value;
  const current = messages.value[idx];
  if (current) {
    current.content += String(data);
    // 触发响应式更新
    messages.value[idx] = { ...current };
  }
}

function ensureSseConnected() {
  if (!isSseConnected()) {
    connectSse('/api/', onSseChunk);
  } else {
    setSseHandler(onSseChunk);
  }
}
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #fafbff;
}

.header {
  position: sticky;
  top: 0;
  z-index: 10;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-bottom: 1px solid #eef0f5;
}

.title {
  font-size: 14px;
  color: #2b2f36;
}

.pair-btn {
  position: absolute;
  right: 16px;
  top: 10px;
  height: 32px;
  padding: 0 12px;
  border-radius: 8px;
  border: none;
  background: #eaf2ff;
  color: #5a79ff;
}

.chat {
  flex: 1;
  overflow: auto;
  padding: 24px 0 120px;
}

.message {
  display: flex;
  gap: 8px;
  max-width: 720px;
  margin: 0 auto;
}

.message.me {
  justify-content: flex-end;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eef0f5;
}

.bubble {
  background: white;
  border: 1px solid #eef0f5;
  border-radius: 12px;
  padding: 10px 12px;
  color: #4a5568;
}

.empty-tip {
  max-width: 720px;
  margin: 40px auto;
  color: #667085;
  font-size: 12px;
}

.composer {
  position: sticky;
  bottom: 0;
  background: linear-gradient(to top, #fafbff 60%, rgba(250,251,255,0));
  padding: 16px 0 24px;
}

/* 删除了 .run-btn */

.input-wrap {
  max-width: 760px;
  margin: 0 auto;
  background: white;
  border: 1px solid #eef0f5;
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 6px 20px rgba(20, 40, 160, 0.06);
}

.text-input {
  width: 100%;
  border: none;
  outline: none;
  font-size: 14px;
  color: #2b2f36;
}

.actions {
  margin-top: 10px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.action {
  border: 1px solid #eef0f5;
  background: #f7f9ff;
  color: #5a79ff;
  padding: 6px 10px;
  border-radius: 10px;
}

.action.active {
  background: #e2e9ff;
  border-color: #cfd9ff;
  color: #3b5bfd;
}

.send {
  margin-left: auto;
  width: 28px;
  height: 28px;
  border-radius: 8px;
  border: none;
  background: #4b6bff;
  color: white;
}

.send span {
  display: inline-block;
  font-weight: 700;
}
</style>


