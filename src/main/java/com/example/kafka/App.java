package com.example.kafka;

import com.example.kafka.admin.TopicManager;
import com.example.kafka.config.ConfigLoader;
import com.example.kafka.consumer.OrderConsumer;
import com.example.kafka.consumer.PaymentConsumer;
import com.example.kafka.consumer.NotificationConsumer;
import com.example.kafka.producer.OrderProducer;
import com.example.kafka.model.Order;

public class App {
    public static void main(String[] args) throws InterruptedException {

        ConfigLoader cfg = new ConfigLoader();
        String[] topics = cfg.get("topics").split(",");
        String bootstrapServers = cfg.get("bootstrap.servers");

        // Create topics
        TopicManager topicManager = new TopicManager(bootstrapServers);
        for (String t : topics) {
            topicManager.createTopicIfNotExists(t.trim(), 1, (short) 1);
        }
        topicManager.close();

        // Start consumers
        new Thread(() -> new OrderConsumer(bootstrapServers, "orders", "order-group").consume()).start();
        new Thread(() -> new PaymentConsumer(bootstrapServers, "payments", "payment-group").consume()).start();
        new Thread(() -> new NotificationConsumer(bootstrapServers, "notifications", "notification-group").consume()).start();

        // Produce orders only
        OrderProducer orderProducer = new OrderProducer(bootstrapServers, "orders");
        for (int i = 1; i <= 3; i++) {
            orderProducer.sendOrder(new Order("ORD-" + i, i * 1000.0, "NEW"));
            Thread.sleep(1000);
        }
        orderProducer.close();

        System.out.println("Orders produced successfully — rest triggered via Kafka chain!");
    }
}
