package com.example.kafka.model;

public class Order {
    private String orderId;
    private double amount;
    private String status;

    public Order() {}

    public Order(String orderId, double amount, String status) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Order{" + "orderId='" + orderId + '\'' + ", amount=" + amount + ", status='" + status + '\'' + '}';
    }
}
