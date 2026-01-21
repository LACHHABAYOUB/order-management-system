import { Box, Button, Typography } from "@mui/material";

type Props = {
  page: number;
  totalPages: number;
  onPrev: () => void;
  onNext: () => void;
};

export default function PaginationBar({ page, totalPages, onPrev, onNext }: Props) {
  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 2, justifyContent: "flex-end", mt: 2 }}>
      <Typography variant="body2">
        Page {page + 1} / {Math.max(totalPages, 1)}
      </Typography>
      <Button variant="outlined" onClick={onPrev} disabled={page <= 0}>
        Prev
      </Button>
      <Button variant="outlined" onClick={onNext} disabled={page + 1 >= totalPages}>
        Next
      </Button>
    </Box>
  );
}
