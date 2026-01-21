import { Box, Button, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function NotFoundPage() {
  const nav = useNavigate();
  return (
    <Box sx={{ py: 8 }}>
      <Typography variant="h4" sx={{ mb: 1 }}>404</Typography>
      <Typography sx={{ opacity: 0.8, mb: 2 }}>
        Page not found
      </Typography>
      <Button variant="contained" onClick={() => nav("/orders")}>
        Go Orders
      </Button>
    </Box>
  );
}
