package faang.school.postservice.producer;

import faang.school.postservice.dto.kafka.CommentAnalysisEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AnalysisCommentsProducer {

    private final String topic;
    private final KafkaTemplate<String, CommentAnalysisEventDto> stringCommentAnalysisEventDtoKafkaTemplate;

    public AnalysisCommentsProducer(@Value("${spring.topic.analytics}") String topic,
                                    @Qualifier("analyticKafkaTemplate") KafkaTemplate<String, CommentAnalysisEventDto> stringCommentAnalysisEventDtoKafkaTemplate) {
        this.topic = topic;
        this.stringCommentAnalysisEventDtoKafkaTemplate = stringCommentAnalysisEventDtoKafkaTemplate;
    }

    public void publish(CommentAnalysisEventDto event) {

        ///

        stringCommentAnalysisEventDtoKafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent AnalysisEvent [commentId={}] to topic={} partition={}",
                                event.commentId(),
                                topic,
                                result.getRecordMetadata().partition());
                    } else {
                        log.error("Failed sent AnalysisEvent [commentId={}] to topic={}",
                                event.commentId(),
                                topic,
                                ex);
                    }
                });
    }
}