package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.kafka.KafkaLikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.publisher.KafkaLikeProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.redis.LikeEventPublisher;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final RedisCacheService redisCacheService;
    private final PostMapper postMapper;
    private final LikeEventPublisher likeEventPublisher;
    private final KafkaLikeProducer kafkaPublisher;

    @Transactional
    public LikeDto likePost(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);

        Long postId = likeDto.getPostId();
        Long userId = likeDto.getUserId();

        Post post = postService.findAlredyPublishedAndNotDeletedPost(postId);
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            return likeMapper.toDto(existingLike.get());
        }
        Like like = likeMapper.toModel(likeDto);
        like.setPost(post);

        Like entity = likeRepository.save(like);
        likeEventPublisher.publish(entity);
        log.info("Post id={} was liked by user id={}", likeDto.getPostId(), likeDto.getUserId());

        publishLikeEventToKafka(entity);
        return likeMapper.toDto(entity);
    }

    public void unlikePost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Post id={} was unliked by user id={}", postId, userId);
    }

    public LikeDto likeComment(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Long commentId = likeDto.getCommentId();
        Long userId = likeDto.getUserId();
        Comment comment = commentService.getComment(likeDto.getCommentId());
        Optional<Like> existingLike = likeRepository.findByCommentIdAndUserId(commentId, userId);
        if (existingLike.isPresent()) {
            return likeMapper.toDto(existingLike.get());
        }
        Like like = likeMapper.toModel(likeDto);
        like.setComment(comment);
        likeRepository.save(like);
        log.info("Comment id={} was liked by user id={}", likeDto.getCommentId(), likeDto.getUserId());
        return likeMapper.toDto(like);
    }

    public void unlikeComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Comment id={} was unliked by user id={}", commentId, userId);
    }

    private void publishLikeEventToKafka(Like like) {
        long postId = like.getPost().getId();
        long authorId = like.getUserId();

        KafkaLikeEvent event = KafkaLikeEvent.builder()
                .postId(postId)
                .authorId(authorId)
                .build();
        kafkaPublisher.publish(event);
    }

    @Async("likeTaskExecutor")
    public void incrementRedisPostLike(long postId) {
        log.info("Attempting to increment post like to Post with ID: {}", postId);

        Optional<RedisPost> redisPost = redisCacheService.findByRedisPostBy(postId);
        if (redisPost.isPresent()) {
            log.info("Incrementing post like in Redis with Post ID: {}", postId);
            RedisPost post = redisPost.get();
            post.incrementPostLike();
            post.incrementPostVersion();

            redisCacheService.updateRedisPost(postId, post);
        } else {
            log.warn("Post with ID {} not found in Redis, attempting to retrieve it from the Database and save it in Redis", postId);
            RedisPost post = postService.mapPostToRedisPost(postService.findPostBy(postId));

            redisCacheService.saveRedisPost(post);
        }
    }
}