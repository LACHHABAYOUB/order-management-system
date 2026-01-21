import { Chip } from "@mui/material";
import type { OrderStatus } from "../types/orders";

export default function StatusChip({ status }: { status: OrderStatus }) {
  const color =
    status === "CREATED" ? "info" :
    status === "PAID" ? "success" :
    "warning";

  return <Chip label={status} color={color} size="small" />;
}
