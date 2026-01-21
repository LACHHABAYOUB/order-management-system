import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Stack,
  Typography
} from "@mui/material";

import ErrorBanner from "../components/ErrorBanner";
import Loading from "../components/Loading";
import { ordersApi } from "../api/ordersApi";
import type { OrderAuditResponse } from "../types/orders";
import { fmtDate } from "../utils/format";

function titleFor(a: OrderAuditResponse): string {
  const from = a.oldStatus ?? "—";
  const to = a.newStatus ?? "—";
  if (a.eventType) return `${a.eventType}: ${from} → ${to}`;
  return `${from} → ${to}`;
}

function metaFor(a: OrderAuditResponse): string {
  const who = a.changedBy ? `by ${a.changedBy}` : "";
  return [fmtDate(a.changedAt), who].filter(Boolean).join(" • ");
}

export default function OrderAuditPage() {
  const { id } = useParams();
  const nav = useNavigate();

  const [items, setItems] = useState<OrderAuditResponse[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<unknown>(null);

  const load = async () => {
    if (!id) return;
    setError(null);
    setLoading(true);
    try {
      const res = await ordersApi.audit(id);
      setItems(res);
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

  const has = useMemo(() => (items && items.length > 0) || false, [items]);

  if (!id) return <Typography>Missing id</Typography>;

  return (
    <Box sx={{ maxWidth: 900 }}>
      <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 2 }}>
        <Typography variant="h5">Audit</Typography>
        <Stack direction="row" gap={1}>
          <Button variant="outlined" onClick={() => void load()} disabled={loading}>
            Refresh
          </Button>
          <Button variant="outlined" onClick={() => nav(`/orders/${id}`)}>
            Back
          </Button>
        </Stack>
      </Stack>

      <ErrorBanner error={error} title="Failed to load audit" />
      {loading && !items ? <Loading /> : null}

      {items && items.length === 0 ? (
        <Typography sx={{ opacity: 0.8 }}>No audit records.</Typography>
      ) : null}

      {has ? (
        <Typography variant="body2" sx={{ opacity: 0.75, mb: 2 }}>
          Latest first (descending by time).
        </Typography>
      ) : null}

      <Stack gap={2}>
        {(items || []).map((a, idx) => (
          <Card key={idx}>
            <CardContent>
              <Stack direction="row" justifyContent="space-between" alignItems="center" gap={2}>
                <Box>
                  <Typography variant="subtitle2" sx={{ opacity: 0.85 }}>
                    {metaFor(a)}
                  </Typography>
                  <Typography variant="h6" sx={{ mt: 0.5 }}>
                    {titleFor(a)}
                  </Typography>
                </Box>

                {a.eventType ? (
                  <Chip label={a.eventType} variant="outlined" />
                ) : null}
              </Stack>

              {a.note ? (
                <Typography variant="body2" sx={{ mt: 1, opacity: 0.85 }}>
                  Note: {a.note}
                </Typography>
              ) : null}
            </CardContent>
          </Card>
        ))}
      </Stack>
    </Box>
  );
}
