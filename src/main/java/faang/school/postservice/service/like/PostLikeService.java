package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.PostLikeDto;
import faang.school.postservice.kafka.event.State;
import faang.school.postservice.kafka.event.like.PostLikeKafkaEvent;
import faang.school.postservice.redis.pubsub.event.LikeRedisEvent;
import faang.school.postservice.mapper.like.PostLikeMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostLike;
import faang.school.postservice.kafka.producer.like.PostLikeProducer;
import faang.school.postservice.redis.pubsub.publisher.LikeEventPublisher;
import faang.school.postservice.repository.PostLikeRepository;
import faang.school.postservice.validator.like.LikeValidatorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService implements LikeService<PostLikeDto> {

    private final PostLikeRepository postLikeRepository;
    private final PostLikeMapper postLikeMapper;
    private final LikeValidatorImpl likeValidator;
    private final LikeEventPublisher likeEventPublisher;
    private final PostLikeProducer postLikeProducer;

    @Override
    @Transactional
    public PostLikeDto addLike(long userId, long id) {

        PostLikeDto likeDto = createLikeDto(userId, id);

        likeValidator.validateUserExistence(userId);
        Post post = likeValidator.validateAndGetPostToLike(userId, id);

        PostLike like = postLikeMapper.toEntity(likeDto);
        like.setPost(post);
        like = postLikeRepository.save(like);

        likeEventPublisher.publish(new LikeRedisEvent(id, post.getAuthorId(), userId, LocalDateTime.now()));
        PostLikeKafkaEvent kafkaEvent = postLikeMapper.toKafkaEvent(like, State.ADD);
        postLikeProducer.produce(kafkaEvent);

        log.info("Like with likeId = {} was added on post with postId = {} by user with userId = {}", like.getId(), id, userId);

        return postLikeMapper.toDto(like);
    }

    @Override
    @Transactional
    public void removeLike(long userId, long id) {

        PostLikeDto likeDto = createLikeDto(userId, id);
        PostLike like = postLikeMapper.toEntity(likeDto);

        postLikeRepository.deleteByPostIdAndUserId(id, userId);

        postLikeProducer.produce(postLikeMapper.toKafkaEvent(like, State.DELETE));

        log.info("Like with likeId = {} was removed from post with postId = {} by user with userId = {}", like.getId(), id, userId);
    }

    private PostLikeDto createLikeDto(Long userId, Long postId) {
        PostLikeDto likeDto = new PostLikeDto();
        likeDto.setUserId(userId);
        likeDto.setPostId(postId);
        return likeDto;
    }
}
