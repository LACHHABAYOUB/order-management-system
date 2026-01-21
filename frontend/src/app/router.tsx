import React from "react";
import { createBrowserRouter, Navigate } from "react-router-dom";
import AppShell from "../components/AppShell";
import LoginPage from "../pages/LoginPage";
import OrdersPage from "../pages/OrdersPage";
import OrderCreatePage from "../pages/OrderCreatePage";
import OrderDetailsPage from "../pages/OrderDetailsPage";
import OrderAuditPage from "../pages/OrderAuditPage";
import NotFoundPage from "../pages/NotFoundPage";
import { getApiKey } from "./storage";

function requireAuth(element: React.ReactNode) {
  const k = getApiKey();
  if (!k) return <Navigate to="/login" replace />;
  return element;
}

export const router = createBrowserRouter([
  {
    path: "/",
    element: <AppShell />,
    errorElement: <NotFoundPage />,
    children: [
      { index: true, element: requireAuth(<Navigate to="/orders" replace />) },
      { path: "login", element: <LoginPage /> },
      { path: "orders", element: requireAuth(<OrdersPage />) },
      { path: "orders/new", element: requireAuth(<OrderCreatePage />) },
      { path: "orders/:id", element: requireAuth(<OrderDetailsPage />) },
      { path: "orders/:id/audit", element: requireAuth(<OrderAuditPage />) },
      { path: "*", element: <NotFoundPage /> }
    ]
  }
]);
