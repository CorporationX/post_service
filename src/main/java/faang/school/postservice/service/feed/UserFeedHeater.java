package faang.school.postservice.service.feed;

import faang.school.postservice.client.SubscriptionServiceClient;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFeedHeater {

    private final UserFeedHeaterInternal heaterInternal;
    private final SubscriptionServiceClient subscriptionServiceClient;

    @Async("feedHeaterExecutor")
    public void heatUser(UserDto user) {
        try {
            List<Long> followeeIds = subscriptionServiceClient.getFolloweeIds(user.id());

            if (followeeIds.isEmpty()) {
                log.debug("Skipping feed heating for user {}: no followees", user.id());
                return;
            }

            heaterInternal.heatUserInTransaction(user, followeeIds);
        } catch (Exception e) {
            log.error("User feed warmup error {}", user.id(), e);
        }
    }
}
