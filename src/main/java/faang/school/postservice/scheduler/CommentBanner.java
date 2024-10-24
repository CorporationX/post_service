package faang.school.postservice.scheduler;

import faang.school.postservice.redis.publisher.MessagePublisher;
import faang.school.postservice.redis.publisher.dto.AuthorBanDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentBanner {
    private final CommentService commentService;
    private final MessagePublisher publisher;

    @Scheduled(cron = "${comment.banner.scheduler.cron}")
    public void retrieveAndPublishViolatingAuthorIds() {
        List<Long> authorIdsToBeBanned = commentService.getAuthorIdsToBeBanned();
        log.info("Sending ids for authors to be banned: {}", authorIdsToBeBanned);
        authorIdsToBeBanned.stream()
                .map(AuthorBanDto::new)
                .forEach(publisher::publish);
    }
}
