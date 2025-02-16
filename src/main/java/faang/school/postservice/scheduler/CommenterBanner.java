package faang.school.postservice.scheduler;

import faang.school.event.Event;
import faang.school.event.UserBanEvent;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
public class CommenterBanner {

    private final CommentService commentService;
    private final KafkaTemplate<String, Event> kafkaTemplate;

    @Value("${spring.kafka.topics.user-ban-topic.name}")
    private String userBanTopicName;

    @Scheduled(cron = "${commenter-banner.cron}")
    public void runBannerTask() {
        List<Long> authorIdsForBan = commentService.findAuthorIdsForBan();
        authorIdsForBan.forEach(authorId -> {
            Event event = UserBanEvent.builder()
                    .userId(authorId)
                    .banned(true)
                    .build();

            kafkaTemplate.send(userBanTopicName, event);
        });
    }
}
