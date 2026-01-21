package com.ayoub.orders.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateOrderRequest {

    @NotBlank(message = "customerName is required")
    @Size(min = 2, max = 80, message = "customerName must be between 2 and 80 characters")
    private String customerName;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
