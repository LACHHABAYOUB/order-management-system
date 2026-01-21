export type ApiError = {
  timestamp?: string;
  path?: string;
  error?: string;
  message?: string;
  details?: Record<string, unknown>;
};

export class HttpError extends Error {
  status: number;
  url: string;
  body?: unknown;

  constructor(status: number, url: string, message: string, body?: unknown) {
    super(message);
    this.status = status;
    this.url = url;
    this.body = body;
  }
}
