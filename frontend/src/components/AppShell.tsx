import { Outlet, useLocation, useNavigate } from "react-router-dom";
import {
  AppBar,
  Box,
  Button,
  Container,
  Toolbar,
  Typography
} from "@mui/material";
import { clearApiKey, getApiKey } from "../app/storage";

export default function AppShell() {
  const nav = useNavigate();
  const loc = useLocation();
  const authed = !!getApiKey();

  const onLogout = () => {
    clearApiKey();
    nav("/login");
  };

  const go = (path: string) => () => nav(path);

  return (
    <Box sx={{ minHeight: "100vh" }}>
      <AppBar position="sticky" elevation={0} color="transparent">
        <Toolbar sx={{ gap: 2 }}>
          <Typography
            variant="h6"
            sx={{ cursor: "pointer", userSelect: "none" }}
            onClick={go("/orders")}
          >
            Order Service UI
          </Typography>

          <Box sx={{ flex: 1 }} />

          {authed && loc.pathname !== "/login" ? (
            <>
              <Button color="inherit" onClick={go("/orders")}>Orders</Button>
              <Button color="inherit" onClick={go("/orders/new")}>Create</Button>
              <Button color="inherit" onClick={onLogout}>Logout</Button>
            </>
          ) : (
            <Button color="inherit" onClick={go("/login")}>Login</Button>
          )}
        </Toolbar>
      </AppBar>

      <Container sx={{ py: 3, maxWidth: "lg" }}>
        <Outlet />
      </Container>
    </Box>
  );
}
