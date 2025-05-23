package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeater {

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final FeedService feedService;

    private final UserFeedHeater userFeedHeater;

    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    public void heatFeeds() {
        log.info("🔥 Прогрев фидов начат");
        int page = 0;
        int size = 1000;

        while (true) {
            List<UserDto> users = userServiceClient.getUsersByPage(page, size);
            if (users.isEmpty()) break;

            for (UserDto user : users) {
                userFeedHeater.heatUser(user);
            }

            log.info("📄 Прогрев страницы пользователей: page={}", page);
            page++;
        }
    }
}
