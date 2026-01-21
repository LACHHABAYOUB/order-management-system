import { useMemo, useState } from "react";
import { Box, Button, Card, CardContent, TextField, Typography } from "@mui/material";
import { setApiKey, getApiKey } from "../app/storage";
import { useNavigate } from "react-router-dom";
import ErrorBanner from "../components/ErrorBanner";
import { ordersApi } from "../api/ordersApi";

export default function LoginPage() {
  const nav = useNavigate();
  const [apiKey, setKey] = useState(getApiKey() || "");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<unknown>(null);

  const disabled = useMemo(() => !apiKey.trim() || loading, [apiKey, loading]);

  const onSave = async () => {
    setError(null);
    setLoading(true);
    try {
      setApiKey(apiKey.trim());


      await ordersApi.list({ page: 0, size: 1 });

      nav("/orders");
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ maxWidth: 520, mx: "auto", mt: 6 }}>
      <Card>
        <CardContent>
          <Typography variant="h5" sx={{ mb: 1 }}>
            Login
          </Typography>
          <Typography variant="body2" sx={{ opacity: 0.8, mb: 2 }}>
            Enter your API key. We will send it as <b>X-API-KEY</b> in every request.
          </Typography>

          <ErrorBanner error={error} />

          <TextField
            fullWidth
            label="API Key"
            value={apiKey}
            onChange={(e) => setKey(e.target.value)}
            placeholder="secret-api-key"
            autoFocus
            sx={{ mb: 2 }}
          />

          <Button
            fullWidth
            variant="contained"
            onClick={onSave}
            disabled={disabled}
          >
            {loading ? "Checking..." : "Save & Continue"}
          </Button>
        </CardContent>
      </Card>
    </Box>
  );
}
