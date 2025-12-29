package faang.school.postservice.controller;

import faang.school.postservice.event.FeedHeaterEvent;
import faang.school.postservice.publisher.FeedHeaterEventPublisher;
import faang.school.postservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedHeaterController {

    @Value("${feed-heater.followers-partition-size}")
    private int partitionSize;

    private final SubscriptionRepository subscriptionRepository;
    private final FeedHeaterEventPublisher feedHeaterEventPublisher;

    @PostMapping("/heat")
    public ResponseEntity<Void> heat() {
        List<Long> followerIds = subscriptionRepository.findFollowerIds();
        List<List<Long>> batches = ListUtils.partition(followerIds, partitionSize);

        for (List<Long> batch : batches) {
            FeedHeaterEvent feedHeaterEvent = FeedHeaterEvent.builder()
                    .userIds(batch)
                    .build();
            feedHeaterEventPublisher.publish(feedHeaterEvent);
        }

        return ResponseEntity.ok().build();
    }
}