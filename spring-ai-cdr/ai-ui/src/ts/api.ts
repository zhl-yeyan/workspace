export type ChatPayload = {
  text: string;
  deepThink: boolean;
  webSearch: boolean;
  timestamp: number;
};

/**
 * 发送聊天请求到后端 /chat/doChat，由 SSE 推送结果。
 */
export async function sendChat(payload: ChatPayload & { userId?: string }): Promise<void> {
  const res = await fetch(`/api/chat/doChat`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });
  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }
}


