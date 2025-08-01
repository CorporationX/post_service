package faang.school.postservice.newsfeed;

import com.google.common.collect.Lists;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.newsfeed.dto.KafkaTransportDto;
import faang.school.postservice.newsfeed.events.PostPublishEvent;
import faang.school.postservice.newsfeed.repository.EventProcessedRepository;
import faang.school.postservice.newsfeed.service.CacheService;
import faang.school.postservice.newsfeed.util.mapping.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsFeedProcessor {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper mapper;
    private final EventProcessedRepository eventProcessedRepository;
    private final UserServiceClient userServiceClient;
    private final CacheService cacheService;

    private static final String PREPOST_TOPIC = "prepost";
    private static final String POST_TOPIC = "posts";
    private static final int FOLLOWERS_SPLIT_NUMBER = 1000;

    @Async("newsFeedExecutor")
    public void postPublish(PostOutputDto postOutputDto) {
        String uuid = UUID.randomUUID().toString();
        String json = mapper.mapToJson(postOutputDto);
        KafkaTransportDto dto = new KafkaTransportDto(uuid, json);
        String postPublishEventPayload = mapper.mapToJson(dto);
        kafkaTemplate.send(PREPOST_TOPIC, uuid, postPublishEventPayload);
        cacheService.findAndPutUserToCache(postOutputDto.getAuthorId());
        cacheService.putPostToCache(postOutputDto);
    }

    @Async("newsFeedExecutor")
    @Transactional
    @KafkaListener(topics = PREPOST_TOPIC, groupId = "new_post_preprocess", concurrency = "3")
    public void postPublishEventProcess(KafkaTransportDto dto) {
        UUID eventUuid = UUID.fromString(dto.uniqEventIdentifier());
        switch (eventProcessedRepository.registerEventId(eventUuid)) {
            case (0) -> {
                log.debug("Event {} already processed", eventUuid);
            }
            case (1) -> {
                PostOutputDto postOutputDto = mapper.mapToObject(dto.payload(), PostOutputDto.class);
                Long authorId = postOutputDto.getAuthorId();
                List<Long> followerIds = userServiceClient.getFollowerIds(authorId);
                List<List<Long>> partitions = Lists.partition(followerIds, FOLLOWERS_SPLIT_NUMBER);
                Map<UUID, String> kafkaEventPayloads = partitions.stream()
                        .map(sublist -> new PostPublishEvent(authorId, postOutputDto.getId(), sublist))
                        .map(mapper::mapToJson)
                        .collect(Collectors.toMap(UUID::fromString, s -> s));
                kafkaEventPayloads.forEach((key, value) -> kafkaTemplate.send(POST_TOPIC, key.toString(), value));
            }
            default -> {
                log.error("Unsuccessful attempt to save uuid {}", eventUuid);
            }
        }
    }

}
