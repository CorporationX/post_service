package faang.school.postservice.producer.commentanalysis;

import faang.school.postservice.dto.commentanalysis.AnalysisCommentsEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AnalysisCommentsProducer {

    private final String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AnalysisCommentsProducer(@Value("${spring.topic.analytics}") String topic,
                                    KafkaTemplate<String, Object> stringCommentAnalysisEventDtoKafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = stringCommentAnalysisEventDtoKafkaTemplate;
    }

    public void publish(AnalysisCommentsEventDto dto) {

        kafkaTemplate.send(topic, dto)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent AnalysisEvent [commentId={}] to topic={} partition={}",
                                dto.commentId(),
                                topic,
                                result.getRecordMetadata().partition());
                    } else {
                        log.error("Failed sent AnalysisEvent [commentId={}] to topic={}",
                                dto.commentId(),
                                topic,
                                ex);
                    }
                });
    }
}