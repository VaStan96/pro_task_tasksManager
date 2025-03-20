package pro_task_tasksmanager.tasksmanager.Kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TaskProducer.class);

    @Value("${task.topic.name.create}")
    private String topicNameCreate;

    @Value("${task.topic.name.delete}")
    private String topicNameDelete;

    public TaskProducer(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskCreateNotification(Object message){
        logger.info("Attempting to send message to Kafka. Topic: {}, Message: {}", topicNameCreate, message);
        kafkaTemplate.send(topicNameCreate, message)
            .thenAccept(sendResult -> {
                logger.info("Message sent successfully. Topic: {}, Partition: {}, Offset: {}",
                        sendResult.getRecordMetadata().topic(),
                        sendResult.getRecordMetadata().partition(),
                        sendResult.getRecordMetadata().offset());
            })
            .exceptionally(ex -> {
                logger.error("Failed to send message to Kafka. Error: {}", ex.getMessage());
                return null;
            });
    }

    public void sendTaskDeleteNotification(Object message){
        logger.info("Attempting to send message to Kafka. Topic: {}, Message: {}", topicNameDelete, message);
        kafkaTemplate.send(topicNameDelete, message)
            .thenAccept(sendResult -> {
                logger.info("Message sent successfully. Topic: {}, Partition: {}, Offset: {}",
                        sendResult.getRecordMetadata().topic(),
                        sendResult.getRecordMetadata().partition(),
                        sendResult.getRecordMetadata().offset());
            })
            .exceptionally(ex -> {
                logger.error("Failed to send message to Kafka. Error: {}", ex.getMessage());
                return null;
            });
    }
}
