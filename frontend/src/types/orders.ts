export type OrderStatus = "CREATED" | "PAID" | "CANCELLED";

export type OrderResponse = {
  id: string;
  customerName: string;
  status: OrderStatus;
  createdAt: string;
  paidAt?: string | null;
  cancelledAt?: string | null;
  cancelReason?: string | null;
  version?: number;
};

export type CreateOrderRequest = {
  customerName: string;
};

export type UpdateOrderStatusRequest = {
  status: OrderStatus;
  reason?: string;
};

export type Page<T> = {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};

export type OrderAuditResponse = {
  oldStatus?: string | null;
  newStatus?: string | null;
  changedAt: string;
  changedBy?: string | null;
  eventType?: string | null;
  note?: string | null;
};
