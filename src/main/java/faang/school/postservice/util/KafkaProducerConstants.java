package faang.school.postservice.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaProducerConstants {
    private final String COMMENT_EVENT = "comment_event:faang.school.postservice.model.event.kafka.CommentEventKafka, ";
    private final String POST_EVENT = "post_event:faang.school.postservice.model.event.kafka.PostEventKafka, ";
    private final String POST_VIEW_EVENT = "post_view_event:faang.school.postservice.model.event.kafka.PostViewEventKafka, ";
    private final String HEAT_TASK_EVEN = "heat_task_event:faang.school.postservice.model.event.kafka.HeatTaskEventKafka, ";
    private final String LIKE_EVENT = "like_event:faang.school.postservice.model.event.kafka.PostLikeEventKafka";
}
