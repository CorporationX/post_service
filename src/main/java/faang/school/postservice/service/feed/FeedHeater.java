package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.properties.feed.FeedHeaterProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeater {

    private final UserServiceClient userServiceClient;
    private final UserFeedHeater userFeedHeater;
    private final FeedHeaterProperties heaterProperties;

    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    public void heatFeeds() {
        log.info("Feed warm-up has begun");
        int page = 0;
        int size = heaterProperties.getPageSize();

        while (true) {
            List<UserDto> users = userServiceClient.getUsersByPage(page, size);
            if (users.isEmpty()) break;

            for (UserDto user : users) {
                userFeedHeater.heatUser(user);
            }

            log.info("Warming up the user page: page={}", page);
            page++;
        }
    }
}
