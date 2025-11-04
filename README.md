# 🧩 Kafka Demo Project

## Overview
This **Kafka Demo Project** demonstrates how multiple microservices (Order, Payment, and Notification) communicate asynchronously using **Apache Kafka**.  
It covers essential Kafka concepts such as:
- Producer and Consumer APIs
- Topic creation programmatically
- JSON serialization and deserialization
- Multi-topic communication pattern
- Threaded consumer setup

## 🏗️ Architecture
```
 ┌────────────┐        ┌─────────────┐        ┌────────────────┐
 │ Order App  │──►(orders topic)──►│ Payment App │──►(payments topic)──►│ Notification App │
 └────────────┘        └─────────────┘        └────────────────┘
```

Each service is represented by a **Producer** and a **Consumer** pair:
- `OrderProducer` → Sends new order messages  
- `PaymentConsumer` → Processes payments upon receiving orders  
- `PaymentProducer` → Publishes payment confirmation  
- `NotificationConsumer` → Sends notifications based on payments  
- `NotificationProducer` → Logs / confirms successful notifications  

## ⚙️ Technologies Used
- **Java 17+**
- **Apache Kafka 3.x**
- **Maven**
- **Jackson** (for JSON serialization/deserialization)
- **Eclipse IDE / IntelliJ IDEA**

## 🧾 Project Structure

```
kafka-demo/
├── src/main/java/com/example/kafka/
│   ├── admin/TopicManager.java
│   ├── config/ConfigLoader.java
│   ├── consumer/
│   │   ├── OrderConsumer.java
│   │   ├── PaymentConsumer.java
│   │   └── NotificationConsumer.java
│   ├── model/
│   │   ├── Order.java
│   │   ├── Payment.java
│   │   └── Notification.java
│   ├── producer/
│   │   ├── OrderProducer.java
│   │   ├── PaymentProducer.java
│   │   └── NotificationProducer.java
│   └── App.java
├── resources/
│   └── application.properties
└── pom.xml
```

## ⚡ How It Works

1. **Topics are created programmatically**  
   Using `TopicManager`, the app ensures that all required topics exist:
   ```java
   topicManager.createTopicIfNotExists("orders", 1, (short)1);
   topicManager.createTopicIfNotExists("payments", 1, (short)1);
   topicManager.createTopicIfNotExists("notifications", 1, (short)1);
   ```

2. **Producers send messages**
   Each producer serializes its domain object (Order, Payment, Notification) into JSON and sends it to Kafka.

3. **Consumers run in separate threads**
   Each consumer listens to its assigned topic, deserializes the JSON, and processes the message.

4. **Chained event flow**
   - OrderProducer → sends new orders  
   - PaymentConsumer → consumes orders, processes payment  
   - PaymentProducer → sends payment success  
   - NotificationConsumer → consumes payments, sends notification  

## 🧪 Running the Application

### 1. Start Kafka and Zookeeper
If you’re running Kafka locally:
```bash
cd C:\kafka
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

### 2. Check topics (optional)
```bash
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

### 3. Run the Java Application
You can run `App.java` from Eclipse or via terminal:
```bash
mvn clean package
java -jar target/kafka-demo-1.0-SNAPSHOT.jar
```

### 4. Observe the logs
You’ll see:
```
✅ All topics created successfully!
✅ Consumers started...
✅ Sending Orders...
✅ Sending Payments...
✅ Sending Notifications...
✅ All messages sent successfully!
```

## 🧰 Configuration (application.properties)
Example config:
```properties
bootstrap.servers=localhost:9092
topics=orders,payments,notifications
```

## 🚀 Next Steps (Future Enhancements)
- Add schema validation using **Avro**  
- Introduce **Dead Letter Queue (DLQ)** handling  
- Use **Kafka Streams** for real-time transformation  
- Containerize with **Docker Compose**  
- Add **unit/integration tests** with Testcontainers  

## 👨‍💻 Author
**Mukti Gosavi**  
Kafka Demo for multi-service event-driven architecture  
💡 *Developed as a learning and reference project for Apache Kafka integrations.*
