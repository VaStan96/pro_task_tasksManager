package pro_task_tasksmanager.tasksmanager.Kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${task.topic.name}")
    private String topicName;

    public TaskProducer(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskNotification(Object message){
        kafkaTemplate.send(topicName, message);
    }

}
