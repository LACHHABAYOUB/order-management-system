CREATE TABLE IF NOT EXISTS orders (
  id UUID PRIMARY KEY,
  version BIGINT NOT NULL DEFAULT 0,
  customer_name VARCHAR(80) NOT NULL,
  status VARCHAR(30) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  paid_at TIMESTAMPTZ NULL,
  cancelled_at TIMESTAMPTZ NULL,
  cancel_reason VARCHAR(200) NULL
);

CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_status_created_at ON orders(status, created_at DESC);

CREATE TABLE IF NOT EXISTS order_audit (
  id BIGSERIAL PRIMARY KEY,
  order_id UUID NOT NULL,
  event_type VARCHAR(40) NOT NULL,
  from_status VARCHAR(30),
  to_status VARCHAR(30),
  note VARCHAR(250),
  created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_order_audit_order_id_created_at ON order_audit(order_id, created_at DESC);

CREATE TABLE IF NOT EXISTS order_idempotency (
  idempotency_key VARCHAR(80) PRIMARY KEY,
  request_hash VARCHAR(64) NOT NULL,
  order_id UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_order_idempotency_order_id ON order_idempotency(order_id);
CREATE TABLE order_audit (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    old_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL,
    changed_by VARCHAR(80)
);

CREATE INDEX idx_order_audit_order_id ON order_audit(order_id);
CREATE INDEX idx_order_audit_changed_at ON order_audit(changed_at DESC);
