<template>
  <div class="page">
    <header class="header">
      <div class="title">新对话</div>
      <button class="pair-btn">对对</button>
    </header>

    <main class="chat" ref="chatRef">
      <div v-if="messages.length === 0" class="empty-tip"></div>
      <div
        v-for="(m, idx) in messages"
        :key="idx"
        class="message"
        :class="{ me: m.role === 'user' }"
      >
        <div v-if="!m.html" class="bubble">{{ m.content }}</div>
        <div v-else class="bubble" v-html="m.html"></div>
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
            :aria-pressed="enableDeepThink"
          >深度思考</button>
          <button
            class="action"
            :class="{ active: enableWebSearch }"
            @click="enableWebSearch = !enableWebSearch"
            :aria-pressed="enableWebSearch"
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
import { ref, computed, onBeforeUnmount, nextTick } from 'vue';
import { marked } from 'marked';
import { connectSse, closeSse, isSseConnected, setSseHandler } from './services/sse';
import { sendChat } from './ts/api';

const input = ref('');
const showSquare = ref(false);
const enableDeepThink = ref(false);
const enableWebSearch = ref(false);
type ChatMessage = { role: 'user' | 'assistant'; content: string; html?: string };
const messages = ref<ChatMessage[]>([]);
const streamingAssistantIndex = ref<number | null>(null);
const sseUserId = ref<string>('');
const chatRef = ref<HTMLElement | null>(null);

function scrollToBottom() {
  nextTick(() => {
    const el = chatRef.value as HTMLElement | null;
    if (el) {
      el.scrollTo({ top: el.scrollHeight, behavior: 'smooth' });
    }
  });
}

function renderMarkdownIntoMessage(index: number, text: string) {
  const result = marked.parse(text);
  const applyHtml = (html: string) => {
    const current = messages.value[index];
    if (!current) return;
    current.html = html;
    current.content = '';
    messages.value[index] = { ...current };
  };
  if (result && typeof (result as any)?.then === 'function') {
    (result as Promise<string>).then((html) => {
      applyHtml(html);
      scrollToBottom();
    });
  } else {
    applyHtml(result as string);
  }
}

function buildSseUrl(): string {
  if (!sseUserId.value) {
    sseUserId.value = Math.random().toString(36).slice(2, 10);
  }
  return `/api/sse/connect?userId=${encodeURIComponent(sseUserId.value)}`;
}

async function send() {
  const text = input.value.trim();
  if (!text) return;
  messages.value.push({ role: 'user', content: text });
  input.value = '';
  // 新一轮对话开始时，重置流式索引，确保助手消息紧跟其后
  streamingAssistantIndex.value = null;
  scrollToBottom();
  const payload = {
    // 后端需要 ChatEntity 格式：currentUserName/message/botMsgId
    text,
    deepThink: enableDeepThink.value,
    webSearch: enableWebSearch.value,
    timestamp: Date.now(),
  };
  try {
    // 确保已连接 SSE，以接收流式返回
    ensureSseConnected();
    await sendChat({
      // 透传原信息，便于后续扩展
      ...payload,
      userId: sseUserId.value,
      // 映射为后端 ChatEntity
      currentUserName: sseUserId.value,
      message: text,
      botMsgId: ''
    } as any);
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
  connectSse(buildSseUrl(), onSseChunk);
}

function onSseChunk(data: string, event: MessageEvent) {
  const text = String(data);
  // 忽略连接探活等无效内容
  if (text.trim().toLowerCase() === 'connected') return;

  // 结束事件：收尾并为下次回复创建新气泡
  if (event && (event.type === 'finish' || event.type === 'done')) {
    try {
      const parsed = JSON.parse(text);
      const finalMessage: string = parsed?.message ?? '';
      const idx = streamingAssistantIndex.value ?? messages.value.length - 1;
      renderMarkdownIntoMessage(idx, finalMessage);
    } catch {
      // 如果不是 JSON，就按纯文本结束并渲染为 Markdown
      const idx = streamingAssistantIndex.value ?? messages.value.length - 1;
      renderMarkdownIntoMessage(idx, text);
    }
    streamingAssistantIndex.value = null;
    showSquare.value = false;
    scrollToBottom();
    return;
  }

  // 把 SSE 片段追加到同一个助手气泡，实现“流式输出”
  if (streamingAssistantIndex.value == null) {
    streamingAssistantIndex.value = messages.value.length;
    messages.value.push({ role: 'assistant', content: '' });
  }
  const idx = streamingAssistantIndex.value;
  const current = messages.value[idx];
  if (current) {
    current.content += text;
    // 触发响应式更新
    messages.value[idx] = { ...current };
    scrollToBottom();
  }
}

function ensureSseConnected() {
  if (!isSseConnected()) {
    connectSse(buildSseUrl(), onSseChunk);
  } else {
    setSseHandler(onSseChunk);
  }
}

const sseConnected = computed(() => isSseConnected());

onBeforeUnmount(() => {
  closeSse();
});
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


