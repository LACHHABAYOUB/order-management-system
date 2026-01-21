import { useMemo, useState } from "react";
import {
  Box,
  Button,
  Card,
  CardContent,
  IconButton,
  Stack,
  TextField,
  Tooltip,
  Typography
} from "@mui/material";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import AutoFixHighIcon from "@mui/icons-material/AutoFixHigh";

import ErrorBanner from "../components/ErrorBanner";
import { ordersApi } from "../api/ordersApi";
import { useNavigate } from "react-router-dom";
import { uuidV4 } from "../utils/uuid";

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

export default function OrderCreatePage() {
  const nav = useNavigate();
  const [customerName, setCustomerName] = useState("");
  const [idempotencyKey, setIdempotencyKey] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<unknown>(null);

  const canSubmit = useMemo(() => customerName.trim().length >= 2 && !loading, [customerName, loading]);

  const genKey = () => {
    setIdempotencyKey(uuidV4());
  };

  const submit = async () => {
    setError(null);
    setLoading(true);
    try {
      const res = await ordersApi.create(
        { customerName: customerName.trim() },
        idempotencyKey.trim() ? idempotencyKey.trim() : undefined
      );
      nav(`/orders/${res.id}`);
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ maxWidth: 720 }}>
      <Typography variant="h5" sx={{ mb: 2 }}>
        Create Order
      </Typography>

      <ErrorBanner error={error} title="Create failed" />

      <Card>
        <CardContent>
          <Stack gap={2}>
            <TextField
              label="Customer name"
              value={customerName}
              onChange={(e) => setCustomerName(e.target.value)}
              placeholder="Ayoub"
              fullWidth
            />

            <Stack direction={{ xs: "column", md: "row" }} gap={1} alignItems="center">
              <TextField
                label="Idempotency-Key (optional)"
                value={idempotencyKey}
                onChange={(e) => setIdempotencyKey(e.target.value)}
                placeholder="auto-generate..."
                fullWidth
                helperText="Same key twice => same order response (if backend enforces idempotency)."
              />

              <Stack direction="row" gap={1}>
                <Tooltip title="Generate key">
                  <IconButton onClick={genKey}>
                    <AutoFixHighIcon />
                  </IconButton>
                </Tooltip>

                <Tooltip title="Copy key">
                  <span>
                    <IconButton
                      disabled={!idempotencyKey.trim()}
                      onClick={() => void copyToClipboard(idempotencyKey.trim())}
                    >
                      <ContentCopyIcon />
                    </IconButton>
                  </span>
                </Tooltip>
              </Stack>
            </Stack>

            <Stack direction="row" gap={1}>
              <Button variant="contained" onClick={() => void submit()} disabled={!canSubmit}>
                {loading ? "Creating..." : "Create"}
              </Button>
              <Button variant="outlined" onClick={() => nav("/orders")} disabled={loading}>
                Cancel
              </Button>
            </Stack>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
