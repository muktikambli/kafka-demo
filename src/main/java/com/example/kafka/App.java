package com.example.kafka;

import com.example.kafka.admin.TopicManager;
import com.example.kafka.consumer.MessageConsumer;
import com.example.kafka.producer.MessageProducer;

public class App {
    public static void main(String[] args) throws InterruptedException {
        String bootstrapServers = "localhost:9092";
        String topicName = "test-topic";

        // ✅ Step 1: Create topic programmatically
        TopicManager topicManager = new TopicManager(bootstrapServers);
        topicManager.createTopicIfNotExists(topicName, 1, (short) 1);
        topicManager.close();

        // ✅ Step 2: Start consumer in a separate thread
        new Thread(() -> {
            MessageConsumer consumer = new MessageConsumer(bootstrapServers, topicName, "demo-group");
            consumer.consume();
        }).start();

        // ✅ Step 3: Start producer
        MessageProducer producer = new MessageProducer(bootstrapServers, topicName);
        for (int i = 1; i <= 5; i++) {
            producer.sendMessage("key-" + i, "Hello Kafka message " + i);
            Thread.sleep(1000);
        }

        producer.close();
    }
}
