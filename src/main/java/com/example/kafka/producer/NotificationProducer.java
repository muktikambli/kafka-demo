package com.example.kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import com.example.kafka.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Properties;

public class NotificationProducer {
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final ObjectMapper mapper = new ObjectMapper();

    public NotificationProducer(String bootstrapServers, String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }

    public void sendNotification(Notification notification) {
        try {
            String json = mapper.writeValueAsString(notification);
            producer.send(new ProducerRecord<>(topic, notification.getNotificationId(), json),
                    (metadata, exception) -> {
                        if (exception == null) {
                            System.out.printf("Sent notification %s to topic=%s partition=%d offset=%d%n",
                                    notification.getNotificationId(), topic, metadata.partition(), metadata.offset());
                        } else {
                            exception.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        producer.close();
    }
}
