package faang.school.postservice.cosumer.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.comment.CommentKafkaEvent;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.cache.CommentCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentsConsumer {
    private final PostCacheRepository postCacheRepository;
    private final CommentCacheRepository commentCacheRepository;
    private final UserCacheRepository userCacheRepository;

    private final UserMapper userMapper;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @KafkaListener(topics = "${spring.kafka.producer.topics.comments}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenEvent(CommentKafkaEvent event, Acknowledgment acknowledgment) {
        commentCacheRepository.save(event);
        postCacheRepository.addCommentsToPost(event.getPostId());
        addCommentAuthorToCache(event.getAuthorId());
    }

    private void addCommentAuthorToCache(long authorId) {
        if (!userCacheRepository.existsById(authorId)) {
            userContext.setUserId(authorId);
            UserDto userDto =  userServiceClient.getUser(authorId);

            UserRedis userRedis =  userMapper.toRedis(userDto);
            userCacheRepository.save(userRedis);
        }
    }
}
