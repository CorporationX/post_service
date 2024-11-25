package faang.school.postservice.service.feedheater;

import faang.school.postservice.model.event.HeatFeedCacheEvent;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedHeaterImpl implements FeedHeater {
    @Value("${feed.heater.batch-size}")
    private int batchSize;


    private final UserRepository userRepository;
    private final EventPublisher<HeatFeedCacheEvent> distributor;

    @Override
    public void heat() {
        List<Long> userIds = userRepository.getActiveUsersId();
        int i;
        for (i = 0; i < userIds.size() - batchSize; i += batchSize) {
            distributor.publish(new HeatFeedCacheEvent(
                    userIds.subList(i, i + batchSize)
            ));
        }
        distributor.publish(new HeatFeedCacheEvent(
                userIds.subList(i, userIds.size())
        ));
    }
}
