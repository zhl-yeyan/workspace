export type SseOnMessage = (data: string, event: MessageEvent) => void;

let currentSource: EventSource | null = null;
let currentHandler: SseOnMessage | null = null;

export function connectSse(url: string, onMessage: SseOnMessage): EventSource {
  if (currentSource) {
    currentHandler = onMessage;
    return currentSource;
  }
  currentHandler = onMessage;
  const source = new EventSource(url);
  source.onmessage = (event: MessageEvent) => {
    try {
      currentHandler && currentHandler(event.data, event);
    } catch {
      // no-op
    }
  };
  source.onerror = () => {
    // Keep the connection; EventSource will auto-reconnect by default.
  };
  currentSource = source;
  return source;
}

export function closeSse(): void {
  if (currentSource) {
    currentSource.close();
    currentSource = null;
    currentHandler = null;
  }
}

export function isSseConnected(): boolean {
  return !!currentSource;
}

export function setSseHandler(handler: SseOnMessage): void {
  currentHandler = handler;
}


