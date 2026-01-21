import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Box,
  Button,
  Card,
  CardContent,
  Divider,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  Tooltip,
  TextField,
  Typography
} from "@mui/material";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";

import ErrorBanner from "../components/ErrorBanner";
import Loading from "../components/Loading";
import StatusChip from "../components/StatusChip";
import { ordersApi } from "../api/ordersApi";
import type { OrderResponse, OrderStatus } from "../types/orders";
import { fmtDate } from "../utils/format";

async function copyToClipboard(text: string) {
  try {
    await navigator.clipboard.writeText(text);
  } catch {
    const ta = document.createElement("textarea");
    ta.value = text;
    document.body.appendChild(ta);
    ta.select();
    document.execCommand("copy");
    document.body.removeChild(ta);
  }
}

export default function OrderDetailsPage() {
  const { id } = useParams();
  const nav = useNavigate();

  const [order, setOrder] = useState<OrderResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<unknown>(null);

  const [newStatus, setNewStatus] = useState<OrderStatus>("CREATED");
  const [reason, setReason] = useState("");
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<unknown>(null);

  const canCancel = useMemo(() => newStatus === "CANCELLED", [newStatus]);

  const load = async () => {
    if (!id) return;
    setError(null);
    setLoading(true);
    try {
      const res = await ordersApi.getById(id);
      setOrder(res);
      setNewStatus(res.status);
      setReason(res.cancelReason || "");
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const update = async () => {
    if (!id) return;
    setSaveError(null);
    setSaving(true);
    try {
      const res = await ordersApi.updateStatus(id, {
        status: newStatus,
        reason: canCancel ? reason.trim() : undefined
      });
      setOrder(res);
    } catch (e) {
      setSaveError(e);
    } finally {
      setSaving(false);
    }
  };

  if (!id) return <Typography>Missing id</Typography>;

  return (
    <Box sx={{ maxWidth: 900 }}>
      <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 2 }}>
        <Typography variant="h5">Order Details</Typography>
        <Stack direction="row" gap={1}>
          <Button variant="outlined" onClick={() => void load()} disabled={loading}>
            Refresh
          </Button>
          <Button variant="outlined" onClick={() => nav(`/orders/${id}/audit`)}>
            Audit
          </Button>
          <Button variant="outlined" onClick={() => nav("/orders")}>
            Back
          </Button>
        </Stack>
      </Stack>

      <ErrorBanner error={error} title="Failed to load order" />
      {loading && !order ? <Loading /> : null}

      {order ? (
        <Card>
          <CardContent>
            <Stack direction={{ xs: "column", md: "row" }} justifyContent="space-between" gap={2}>
              <Box>
                <Typography variant="h6" sx={{ fontWeight: 800 }}>
                  {order.customerName}
                </Typography>

                <Stack direction="row" alignItems="center" gap={1} sx={{ mt: 0.5 }}>
                  <Typography variant="body2" sx={{ opacity: 0.8 }}>
                    ID: {order.id}
                  </Typography>
                  <Tooltip title="Copy ID">
                    <IconButton size="small" onClick={() => void copyToClipboard(order.id)}>
                      <ContentCopyIcon fontSize="inherit" />
                    </IconButton>
                  </Tooltip>
                </Stack>

                <Typography variant="body2" sx={{ opacity: 0.8 }}>
                  Created: {fmtDate(order.createdAt)}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.8 }}>
                  Paid: {fmtDate(order.paidAt ?? null)}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.8 }}>
                  Cancelled: {fmtDate(order.cancelledAt ?? null)}
                </Typography>
              </Box>

              <Stack direction="row" alignItems="center" gap={1}>
                <StatusChip status={order.status} />
              </Stack>
            </Stack>

            <Divider sx={{ my: 2 }} />

            <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1 }}>
              Update status
            </Typography>

            <ErrorBanner error={saveError} title="Update failed" />

            <Stack direction={{ xs: "column", md: "row" }} gap={2} alignItems="flex-start">
              <FormControl sx={{ minWidth: 220 }}>
                <InputLabel>Status</InputLabel>
                <Select
                  label="Status"
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value as OrderStatus)}
                >
                  <MenuItem value="CREATED">CREATED</MenuItem>
                  <MenuItem value="PAID">PAID</MenuItem>
                  <MenuItem value="CANCELLED">CANCELLED</MenuItem>
                </Select>
              </FormControl>

              <TextField
                label="Cancel reason"
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                disabled={!canCancel}
                placeholder="Optional reason"
                fullWidth
              />

              <Button
                variant="contained"
                onClick={() => void update()}
                disabled={saving}
                sx={{ minWidth: 160 }}
              >
                {saving ? "Saving..." : "Save"}
              </Button>
            </Stack>
          </CardContent>
        </Card>
      ) : null}
    </Box>
  );
}
