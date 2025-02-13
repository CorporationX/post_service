package faang.school.postservice.scheduler;

import faang.school.postservice.event.Event;
import faang.school.postservice.event.user_ban.UserBanEvent;
import faang.school.postservice.publisher.user_ban.UserBanEventPublisher;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
public class CommenterBanner {

    private final CommentService commentService;
    private final UserBanEventPublisher userBanEventPublisher;

    @Scheduled(cron = "${commenter-banner.cron}")
    public void runBannerTask() {
        List<Long> authorIdsForBan = commentService.findAuthorIdsForBan();
        authorIdsForBan.forEach(authorId -> {
            Event event = UserBanEvent.builder()
                    .userId(authorId)
                    .banned(true)
                    .build();

            userBanEventPublisher.publishEvent(event);
        });
    }
}
