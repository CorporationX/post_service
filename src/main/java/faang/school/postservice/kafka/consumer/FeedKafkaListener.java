package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.cache.CacheFacade;
import faang.school.postservice.cache.feed.FeedCacher;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedKafkaListener {

    private final CacheFacade<PostCache> postCacheCacheFacade;
    private final PostService postService;
    private final FeedCacher feedCacher;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.feed.name}", groupId = "1")
    public void onMessage(List<FeedDto> messages) {
        log.info("Received message {}", messages.toString());
        var feeds = objectMapper.convertValue(
                messages,
                new TypeReference<List<FeedDto>>() { });
        var postIds = feeds.parallelStream()
                        .flatMap(feedDto -> feedDto.getPostIds().stream())
                        .collect(Collectors.toSet());

        postService.getPostsByIds(postIds)
                .forEach(postCacheCacheFacade::cacheWithDetails);
        feedCacher.cache(feeds);
    }
}
