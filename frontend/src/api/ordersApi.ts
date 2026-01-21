import { http } from "./http";
import type {
  CreateOrderRequest,
  OrderAuditResponse,
  OrderResponse,
  OrderStatus,
  Page,
  UpdateOrderStatusRequest
} from "../types/orders";

export const ordersApi = {
  health: () => http<string>({ path: "/api/v1/hello" }),

  create: (req: CreateOrderRequest, idempotencyKey?: string) =>
    http<OrderResponse>({
      method: "POST",
      path: "/api/v1/orders",
      body: req,
      headers: idempotencyKey ? { "Idempotency-Key": idempotencyKey } : undefined
    }),

  getById: (id: string) =>
    http<OrderResponse>({ path: `/api/v1/orders/${id}` }),

  list: (params: {
    status?: OrderStatus;
    page?: number;
    size?: number;
    sort?: string; // "createdAt,desc"
  }) =>
    http<Page<OrderResponse>>({
      path: "/api/v1/orders",
      query: {
        status: params.status,
        page: params.page ?? 0,
        size: params.size ?? 10,
        sort: params.sort ?? "createdAt,desc"
      }
    }),

  updateStatus: (id: string, req: UpdateOrderStatusRequest) =>
    http<OrderResponse>({
      method: "PATCH",
      path: `/api/v1/orders/${id}/status`,
      body: req
    }),

  audit: (id: string) =>
    http<OrderAuditResponse[]>({
      path: `/api/v1/orders/${id}/audit`
    })
};
