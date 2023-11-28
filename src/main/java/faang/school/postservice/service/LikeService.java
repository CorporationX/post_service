package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.kafka.LikeAction;
import faang.school.postservice.dto.kafka.LikeEvent;
import faang.school.postservice.exception.AlreadyDeletedException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaLikeProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.redis.LikeEventPublisher;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
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
    private final LikeEventPublisher likeEventPublisher;
    private final KafkaLikeProducer kafkaPublisher;

    @Transactional
    public LikeDto likePost(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);

        Long postId = Objects.requireNonNull(likeDto.getPostId(), "Post ID cant be null");
        Long userId = likeDto.getUserId();

        Post post = postService.findPostBy(postId);
        if (post.isDeleted()) {
            throw new AlreadyDeletedException(String.format("Post with ID: %d has been already deleted", postId));
        }
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            return likeMapper.toDto(existingLike.get());
        }
        Like like = likeMapper.toModel(likeDto);
        like.setPost(post);

        Like entity = likeRepository.save(like);
        likeEventPublisher.publish(entity);
        log.info("Post id={} was liked by user id={}", likeDto.getPostId(), likeDto.getUserId());

        publishLikeEventToKafka(postId, null, userId, LikeAction.ADD);
        return likeMapper.toDto(entity);
    }

    @Transactional
    public void unlikePost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        publishLikeEventToKafka(postId, null, userId, LikeAction.REMOVE);
        log.info("Post id={} was unliked by user id={}", postId, userId);
    }

    @Transactional
    public LikeDto likeComment(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);

        Long commentId = Objects.requireNonNull(likeDto.getCommentId(), "Comment ID cant be null");
        Long userId = likeDto.getUserId();

        Comment comment = commentService.getComment(commentId);
        long postId = comment.getPost().getId();

        Optional<Like> existingLike = likeRepository.findByCommentIdAndUserId(commentId, userId);
        if (existingLike.isPresent()) {
            log.warn("User with ID: {} has already liked the comment in Post with ID: {}. Ignoring duplicate like.", userId, postId);
            return likeMapper.toDto(existingLike.get());
        }

        Like like = likeMapper.toModel(likeDto);
        like.setComment(comment);

        Like entity = likeRepository.save(like);
        log.info("Comment id={} was liked by user id={}", commentId, userId);

        publishLikeEventToKafka(postId, commentId, userId, LikeAction.ADD);

        return likeMapper.toDto(entity);
    }

    @Transactional
    public void unlikeComment(long commentId, long userId) {
        likeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresentOrElse(
                        like -> {
                            long postId = like.getComment()
                                    .getPost()
                                    .getId();

                            publishLikeEventToKafka(postId, commentId, userId, LikeAction.REMOVE);

                            likeRepository.deleteByCommentIdAndUserId(commentId, userId);
                            log.info("Comment id={} was unliked by user id={}", commentId, userId);
                        },
                        () -> log.info("Like is already removed from the Comment with ID: {}", commentId)
                );
    }

    private void publishLikeEventToKafka(Long postId, Long commentId, Long authorId, LikeAction likeAction) {
        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .authorId(authorId)
                .commentId(commentId)
                .likeAction(likeAction)
                .build();

        kafkaPublisher.publish(event);
    }
}