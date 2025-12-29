package faang.school.postservice.listener;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.cache.UserCacheDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventListener {
    private final PostCacheRepository postCacheRepository;
    private final UserServiceClient userServiceClient;
    private final UserCacheRepository userCacheRepository;

    @KafkaListener(
            topics = "${kafka.topic.comment-event}",
            containerFactory = "concurrentKafkaCommentListenerFactory"
    )
    public void handleCommentPublishEvent(CommentEventDto commentEventDto, Acknowledgment acknowledgment) {
        log.info("New comment publish event: {}", commentEventDto);
        try {
            UserDto userDto = getUserAuthorId(commentEventDto.authorId());
            userCacheRepository.save(UserCacheDto.builder()
                    .id(userDto.id())
                    .name(userDto.username())
                    .build());
            postCacheRepository.incrementCommentCount(commentEventDto.postId());
        } catch (Exception exception) {
            log.error("Failed to process a comment event: {}", commentEventDto, exception);
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
