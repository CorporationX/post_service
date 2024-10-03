package faang.school.postservice.scheduler.recommendation;

import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HeatRecommendationFeed {

    private final FeedCacheRepository feedCacheRepository;
    private final PostRepository postRepository;

    @Value("${feed.recommendation-size}")
    private int recommendationSize;

    //@Scheduled(cron = "${feed.recommendation-cron}")
    @Scheduled(cron = "*/10 * * * * *")
    public void heatRecommendationFeed() {
        Pageable pageable = PageRequest.of(0, recommendationSize);
        List<Long> recommendation = postRepository.findTopPostIdsByLikes(pageable);
        feedCacheRepository.updateRecommendation(recommendation);
    }
}
