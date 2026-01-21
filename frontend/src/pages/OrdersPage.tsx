import { useEffect, useMemo, useState } from "react";
import {
  Box,
  Button,
  Card,
  CardContent,
  FormControl,
  IconButton,
  InputAdornment,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
  Tooltip,
  Typography
} from "@mui/material";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import ClearIcon from "@mui/icons-material/Clear";
import SearchIcon from "@mui/icons-material/Search";

import { ordersApi } from "../api/ordersApi";
import type { OrderResponse, OrderStatus, Page } from "../types/orders";
import Loading from "../components/Loading";
import ErrorBanner from "../components/ErrorBanner";
import StatusChip from "../components/StatusChip";
import PaginationBar from "../components/PaginationBar";
import { useNavigate } from "react-router-dom";
import { fmtDate } from "../utils/format";
import { useDebouncedValue } from "../utils/useDebouncedValue";

type SortMode = "createdAt,desc" | "createdAt,asc";

async function copyToClipboard(text: string) {
  try {
    await navigator.clipboard.writeText(text);
  } catch {
    // fallback
    const ta = document.createElement("textarea");
    ta.value = text;
    document.body.appendChild(ta);
    ta.select();
    document.execCommand("copy");
    document.body.removeChild(ta);
  }
}

export default function OrdersPage() {
  const nav = useNavigate();
  const [status, setStatus] = useState<OrderStatus | "ALL">("ALL");
  const [sort, setSort] = useState<SortMode>("createdAt,desc");
  const [size, setSize] = useState<number>(10);
  const [page, setPage] = useState<number>(0);

  const [search, setSearch] = useState<string>("");
  const debouncedSearch = useDebouncedValue(search.trim().toLowerCase(), 250);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<unknown>(null);
  const [data, setData] = useState<Page<OrderResponse> | null>(null);

  const queryStatus = useMemo(
    () => (status === "ALL" ? undefined : status),
    [status]
  );

  const load = async () => {
    setError(null);
    setLoading(true);
    try {
      const res = await ordersApi.list({
        status: queryStatus,
        page,
        size,
        sort
      });
      setData(res);
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [status, sort, size, page]);

  const rows = useMemo(() => {
    const base = data?.content ?? [];
    if (!debouncedSearch) return base;
    return base.filter((o) => o.customerName.toLowerCase().includes(debouncedSearch));
  }, [data, debouncedSearch]);

  return (
    <Box>
      <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 2 }}>
        <Typography variant="h5">Orders</Typography>
        <Stack direction="row" gap={1}>
          <Button variant="outlined" onClick={() => void load()} disabled={loading}>
            Refresh
          </Button>
          <Button variant="contained" onClick={() => nav("/orders/new")}>
            Create
          </Button>
        </Stack>
      </Stack>

      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Stack direction={{ xs: "column", md: "row" }} gap={2} alignItems="center">
            <FormControl sx={{ minWidth: 180 }}>
              <InputLabel>Status</InputLabel>
              <Select
                label="Status"
                value={status}
                onChange={(e) => {
                  setPage(0);
                  setStatus(e.target.value as any);
                }}
              >
                <MenuItem value="ALL">ALL</MenuItem>
                <MenuItem value="CREATED">CREATED</MenuItem>
                <MenuItem value="PAID">PAID</MenuItem>
                <MenuItem value="CANCELLED">CANCELLED</MenuItem>
              </Select>
            </FormControl>

            <FormControl sx={{ minWidth: 220 }}>
              <InputLabel>Sort</InputLabel>
              <Select
                label="Sort"
                value={sort}
                onChange={(e) => {
                  setPage(0);
                  setSort(e.target.value as SortMode);
                }}
              >
                <MenuItem value="createdAt,desc">createdAt desc</MenuItem>
                <MenuItem value="createdAt,asc">createdAt asc</MenuItem>
              </Select>
            </FormControl>

            <TextField
              label="Page size"
              type="number"
              value={size}
              onChange={(e) => {
                const v = Number(e.target.value);
                setPage(0);
                setSize(Number.isFinite(v) && v > 0 ? Math.min(v, 100) : 10);
              }}
              sx={{ width: 160 }}
              inputProps={{ min: 1, max: 100 }}
            />

            <TextField
              label="Search customer"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              sx={{ flex: 1, minWidth: 240 }}
              placeholder="type name..."
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon fontSize="small" />
                  </InputAdornment>
                ),
                endAdornment: search ? (
                  <InputAdornment position="end">
                    <IconButton
                      size="small"
                      onClick={() => setSearch("")}
                      aria-label="clear search"
                    >
                      <ClearIcon fontSize="small" />
                    </IconButton>
                  </InputAdornment>
                ) : null
              }}
            />
          </Stack>

          {debouncedSearch ? (
            <Typography variant="body2" sx={{ mt: 1, opacity: 0.75 }}>
              Showing filtered results for: <b>{debouncedSearch}</b>
            </Typography>
          ) : null}
        </CardContent>
      </Card>

      <ErrorBanner error={error} title="Failed to load orders" />
      {loading && !data ? <Loading /> : null}

      <Stack gap={2}>
        {rows.map((o) => (
          <Card key={o.id} sx={{ cursor: "pointer" }} onClick={() => nav(`/orders/${o.id}`)}>
            <CardContent>
              <Stack direction={{ xs: "column", md: "row" }} justifyContent="space-between" gap={2}>
                <Box>
                  <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
                    {o.customerName}
                  </Typography>

                  <Stack direction="row" alignItems="center" gap={1} sx={{ mt: 0.5 }}>
                    <Typography variant="body2" sx={{ opacity: 0.8 }}>
                      ID: {o.id}
                    </Typography>

                    <Tooltip title="Copy ID">
                      <IconButton
                        size="small"
                        onClick={(e) => {
                          e.stopPropagation();
                          void copyToClipboard(o.id);
                        }}
                      >
                        <ContentCopyIcon fontSize="inherit" />
                      </IconButton>
                    </Tooltip>
                  </Stack>

                  <Typography variant="body2" sx={{ opacity: 0.8 }}>
                    Created: {fmtDate(o.createdAt)}
                  </Typography>
                </Box>

                <Stack direction="row" alignItems="center" gap={1}>
                  <StatusChip status={o.status} />
                </Stack>
              </Stack>
            </CardContent>
          </Card>
        ))}
      </Stack>

      {data ? (
        <PaginationBar
          page={data.number}
          totalPages={data.totalPages}
          onPrev={() => setPage((p) => Math.max(0, p - 1))}
          onNext={() => setPage((p) => p + 1)}
        />
      ) : null}
    </Box>
  );
}
