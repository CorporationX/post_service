package faang.school.postservice.producer.commentanalysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.commentanalysis.AnalysisCommentsEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AnalysisCommentsProducer {

    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public AnalysisCommentsProducer(@Value("${spring.topic.analytics}") String topic,
                                    @Qualifier("analyticAndCommentKafkaTemplate") KafkaTemplate<String, String> stringCommentAnalysisEventDtoKafkaTemplate,
                                    ObjectMapper objectMapper) {
        this.topic = topic;
        this.kafkaTemplate = stringCommentAnalysisEventDtoKafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(AnalysisCommentsEventDto dto) {

        String payload;
        payload = mapDtoToString(dto);

        kafkaTemplate.send(topic, payload)
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
    private String mapDtoToString(AnalysisCommentsEventDto dto) {

        String payload;

        try {
            payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return payload;
    }
}