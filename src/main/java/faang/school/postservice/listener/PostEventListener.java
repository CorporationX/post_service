package faang.school.postservice.listener;

import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.cache.UserCacheDto;
import faang.school.postservice.dto.event.PostPublishEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventListener {
    private final FeedCacheRepository feedCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;

    @KafkaListener(
            topics = "${kafka.topic.post-event}",
            containerFactory = "concurrentKafkaPostListenerFactory")
    public void handlePostPublishEvent(PostPublishEventDto postPublishEventDto, Acknowledgment acknowledgment) {
        log.info("New post publish event: {}", postPublishEventDto);
        try {
            postCacheRepository.save(PostCacheDto.builder()
                    .id(postPublishEventDto.postId())
                    .authorId(postPublishEventDto.authorId())
                    .content(postPublishEventDto.content())
                    .createdAt(Instant.now())
                    .build());

            UserDto userDto = getUserAuthorId(postPublishEventDto.authorId());
            userCacheRepository.save(UserCacheDto.builder()
                    .id(userDto.id())
                    .name(userDto.username())
                    .build());

            if (postPublishEventDto.subscriberIds() != null
                    && !postPublishEventDto.subscriberIds().isEmpty()) {
                for (Long subscriberId : postPublishEventDto.subscriberIds()) {
                    feedCacheRepository.save(subscriberId,
                            postPublishEventDto.postId(),
                            Instant.now());
                }
            }
        } catch (Exception exception) {
            log.error("Failed to process a post event {}", postPublishEventDto, exception);
            return;
        }
        acknowledgment.acknowledge();
    }

    private UserDto getUserAuthorId(long userId) {
        ResponseEntity<UserDto> responseEntity = userServiceClient.getUser(userId);
        if (responseEntity.getBody() == null) {
            throw new EntityNotFoundException("User " + userId + " not found");
        }
        return responseEntity.getBody();
    }
}
