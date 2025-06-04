package faang.school.postservice.task;

import faang.school.postservice.publisher.MessagePublisher;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorBanner {
    private final PostService postService;
    private final MessagePublisher userPublisher;

    @Autowired
    AuthorBanner(@Qualifier(value = "redisUserPublisher") MessagePublisher userPublisher, PostService postService) {
        this.postService = postService;
        this.userPublisher = userPublisher;
    }

    @Scheduled(cron = "${cron.project.ban-user}")
    public void publishUsersToBan() {
        List<Long> usersIds = postService.getUsersIdsToBan();
        usersIds.stream()
                .map(String::valueOf)
                .forEach(userPublisher::publish);
    }
}
