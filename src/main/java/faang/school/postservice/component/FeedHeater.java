package faang.school.postservice.component;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.FeedWarmupBatchEvent;
import faang.school.postservice.dto.feed.UserSubscriptionsEvent;
import faang.school.postservice.exception.PageOverflowException;
import faang.school.postservice.exception.UserServiceConnectionException;
import faang.school.postservice.publisher.FeedWarmupEventPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FeedHeater {

    private static final int RETRY_DELAY = 500;
    private static final int RETRY_MULTIPLIER = 3;

    private final UserServiceClient userClient;
    private final FeedWarmupEventPublisher feedWarmupEventPublisher;

    @Value("${batch.users-per-page}")
    private int pageSize;

    public long startHeatingFeed() {
        long usersCount = returnUsersCount();
        long pageCount = (usersCount + pageSize - 1) / pageSize;

        if (pageCount > Integer.MAX_VALUE) {
            throw new PageOverflowException("Too many pages: %d. ", pageCount +
                    "Please, the number of users per page in the setting");
        }

        for (int page = 0; page < (int) pageCount; page++) {
            FeedWarmupBatchEvent event = new FeedWarmupBatchEvent(page, pageSize);
            feedWarmupEventPublisher.publish(event);
        }
        return usersCount;
    }

    public void processUsersFeed(UserSubscriptionsEvent event) {
        Map<Long, List<Long>> userSubscriptions = event.userSubscriptions();
        //TODO: process
    }

    @Retryable(
            retryFor = UserServiceConnectionException.class,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER)
    )
    private long returnUsersCount() {
        try {
            return userClient.getUsersCount();
        } catch (FeignException e) {
            throw new UserServiceConnectionException("Post server returned an error: " + e.getMessage());
        }
    }
}
