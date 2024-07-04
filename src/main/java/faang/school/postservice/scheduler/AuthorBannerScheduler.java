package faang.school.postservice.scheduler;

import faang.school.postservice.event.UserBanEvent;
import faang.school.postservice.publisher.UserBanEventPublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorBannerScheduler {

    private final PostService postService;
    private final UserBanEventPublisher userBanEventPublisher;

    @Scheduled(cron = "${post.user-ban.scheduler.cron}")
    @Async
    public void banUser() {
        List<Long> authorIdsToBan = postService.findAllAuthorIdsWithNotVerifiedPosts().stream()
                .collect(Collectors.groupingBy(elem -> elem, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 5)
                .map(Map.Entry::getKey)
                .toList();

        if (!authorIdsToBan.isEmpty()) {
            userBanEventPublisher.publish(new UserBanEvent(authorIdsToBan));
        }
    }
}
