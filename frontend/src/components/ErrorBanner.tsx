import { Alert, AlertTitle, Box } from "@mui/material";
import { HttpError } from "../types/api";

type Props = {
  error: unknown;
  title?: string;
};

export default function ErrorBanner({ error, title }: Props) {
  if (!error) return null;

  let msg = "Unexpected error";
  let details: string | undefined;

  if (error instanceof HttpError) {
    msg = error.message;
    if (error.body && typeof error.body === "object") {
      try {
        details = JSON.stringify(error.body, null, 2);
      } catch {
        details = undefined;
      }
    }
  } else if (error instanceof Error) {
    msg = error.message;
  } else {
    msg = String(error);
  }

  return (
    <Box sx={{ mb: 2 }}>
      <Alert severity="error" variant="outlined">
        <AlertTitle>{title || "Error"}</AlertTitle>
        {msg}
        {details ? (
          <pre style={{ marginTop: 12, whiteSpace: "pre-wrap" }}>{details}</pre>
        ) : null}
      </Alert>
    </Box>
  );
}
