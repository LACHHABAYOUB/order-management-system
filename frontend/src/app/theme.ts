import { createTheme } from "@mui/material/styles";

export const theme = createTheme({
  palette: {
    mode: "dark"
  },
  shape: {
    borderRadius: 16
  },
  typography: {
    fontFamily: ["Inter", "system-ui", "Segoe UI", "Roboto", "Arial"].join(",")
  }
});
