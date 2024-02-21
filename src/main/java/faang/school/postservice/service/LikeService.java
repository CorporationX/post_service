package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.client.CommentDto;
import faang.school.postservice.dto.kafka.LikePostEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.publisher.KafkaLikeProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.redis.LikeEventPublisher;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final RedisPostRepository redisPostRepository;
    private final KafkaLikeProducer kafkaLikeProducer;
    private final PostService postService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final LikeEventPublisher likeEventPublisher;

    public LikeDto likePost(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Long postId = likeDto.getPostId();
        Long userId = likeDto.getUserId();
        Post post = postMapper.toEntity(postService.getPost(likeDto.getPostId()));
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            return likeMapper.toDto(existingLike.get());
        }
        Like like = likeMapper.toModel(likeDto);
        like.setPost(post);
        likeRepository.save(like);
        likeEventPublisher.publish(like);
        LikePostEvent kafkaLikeEvent = LikePostEvent.builder()
                .postId(postId)
                .authorId(userId)
                .build();
        kafkaLikeProducer.publishLikeEvent(kafkaLikeEvent);
        log.info("Post id={} was liked by user id={}", likeDto.getPostId(), likeDto.getUserId());
        return likeMapper.toDto(like);
    }

    public void unlikePost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Post id={} was unliked by user id={}", postId, userId);
    }

    public LikeDto likeComment(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Long commentId = likeDto.getCommentId();
        Long userId = likeDto.getUserId();
        Optional<Like> existingLike = likeRepository.findByCommentIdAndUserId(commentId, userId);
        if (existingLike.isPresent()) {
            return likeMapper.toDto(existingLike.get());
        }
        CommentDto comment = commentService.getComment(likeDto.getCommentId());
        Like like = likeMapper.toModel(likeDto);
        like.setComment(commentMapper.toEntity(comment));
        likeRepository.save(like);
        log.info("Comment id={} was liked by user id={}", likeDto.getCommentId(), likeDto.getUserId());
        return likeMapper.toDto(like);
    }

    public void unlikeComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Comment id={} was unliked by user id={}", commentId, userId);
    }

    @Transactional
    public void incrementLike(long postId) {
        RedisPost redisPost = redisPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        redisPost.getPostLikes().incrementAndGet();
    }
}
