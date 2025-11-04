package com.example.kafka.admin;

import org.apache.kafka.clients.admin.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class TopicManager {

    private final AdminClient adminClient;

    public TopicManager(String bootstrapServers) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        this.adminClient = AdminClient.create(props);
    }

    /**
     * Create a topic if it doesn't exist already
     */
    public void createTopicIfNotExists(String topicName, int partitions, short replicationFactor) {
        try {
            Set<String> existingTopics = adminClient.listTopics().names().get();
            if (existingTopics.contains(topicName)) {
                System.out.println("Topic already exists: " + topicName);
                return;
            }

            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            System.out.println("Created topic: " + topicName);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        adminClient.close();
    }
}
