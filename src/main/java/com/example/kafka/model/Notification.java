package com.example.kafka.model;

public class Notification {
    private String notificationId;
    private String message;
    private String type; // e.g. "EMAIL", "SMS", "PUSH"

    public Notification() {}

    public Notification(String notificationId, String message, String type) {
        this.notificationId = notificationId;
        this.message = message;
        this.type = type;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
