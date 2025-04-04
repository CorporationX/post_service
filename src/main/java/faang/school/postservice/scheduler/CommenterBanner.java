package faang.school.postservice.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.UserBanEvent;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CommenterBanner {

    private final CommentService commentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.user-ban-topic.name}")
    private String userBanTopicName;

    @Scheduled(cron = "${commenter-banner.cron}")
    public void runBannerTask() {
        List<Long> authorIdsForBan = commentService.findAuthorIdsForBan();
        authorIdsForBan.forEach(authorId -> {
            UserBanEvent userBanEvent = UserBanEvent.builder()
                    .userId(authorId)
                    .banned(true)
                    .build();

            try {
                String userBanJsonEvent = objectMapper.writeValueAsString(userBanEvent);
                kafkaTemplate.send(userBanTopicName, userBanJsonEvent);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
