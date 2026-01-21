const KEY = "order_ui_api_key";

export function getApiKey(): string | null {
  return localStorage.getItem(KEY);
}

export function setApiKey(apiKey: string): void {
  localStorage.setItem(KEY, apiKey);
}

export function clearApiKey(): void {
  localStorage.removeItem(KEY);
}
