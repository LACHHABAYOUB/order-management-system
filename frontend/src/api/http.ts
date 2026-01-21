import { config } from "../app/config";
import { getApiKey, clearApiKey } from "../app/storage";
import { HttpError } from "../types/api";

type RequestOptions = {
  method?: "GET" | "POST" | "PATCH" | "PUT" | "DELETE";
  path: string;
  query?: Record<string, string | number | boolean | undefined | null>;
  body?: unknown;
  headers?: Record<string, string>;
  signal?: AbortSignal;
};

function buildUrl(path: string, query?: RequestOptions["query"]) {
  const base = config.apiBaseUrl.replace(/\/+$/, "");
  const p = path.startsWith("/") ? path : `/${path}`;
  const url = new URL(base + p);
  if (query) {
    Object.entries(query).forEach(([k, v]) => {
      if (v === undefined || v === null) return;
      url.searchParams.set(k, String(v));
    });
  }
  return url.toString();
}

async function parseJsonSafe(res: Response) {
  const ct = res.headers.get("content-type") || "";
  if (!ct.includes("application/json")) {
    const text = await res.text().catch(() => "");
    return text ? { raw: text } : undefined;
  }
  return res.json().catch(() => undefined);
}

export async function http<T>(opts: RequestOptions): Promise<T> {
  const url = buildUrl(opts.path, opts.query);

  const apiKey = getApiKey();
  const headers: Record<string, string> = {
    "Accept": "application/json",
    ...(opts.body ? { "Content-Type": "application/json" } : {}),
    ...(opts.headers || {})
  };

  if (apiKey) {
    headers["X-API-KEY"] = apiKey;
  }

  const res = await fetch(url, {
    method: opts.method || "GET",
    headers,
    body: opts.body ? JSON.stringify(opts.body) : undefined,
    signal: opts.signal
  });

  if (res.status === 401 || res.status === 403) {
    clearApiKey();
  }

  if (!res.ok) {
    const parsed = await parseJsonSafe(res);
    const msg =
      (parsed && (parsed.message || parsed.error)) ||
      `HTTP ${res.status} for ${url}`;
    throw new HttpError(res.status, url, String(msg), parsed);
  }

  const data = await parseJsonSafe(res);
  return data as T;
}
