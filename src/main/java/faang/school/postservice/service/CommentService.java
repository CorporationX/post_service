package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.redis.RedisTopicProperties;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserForNotificationDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.message.event.PostCommentEvent;
import faang.school.postservice.message.poducer.impl.RedisMessagePublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.moderation.sightengine.SightEngineReactiveClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final SightEngineReactiveClient textAnalysisService;
    private final RedisMessagePublisher redisMessagePublisher;
    private final RedisTopicProperties redisTopicProperties;

    @Transactional
    public CommentDto addComment(long postId, CommentDto commentDto) {
        log.info("Trying to add comment: {} to post: {}", commentDto, postId);
        validateUserExists(commentDto.authorId());

        Post post = postService.findPostById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found".formatted(postId)));
        Comment comment = commentMapper.toEntity(commentDto);

        postService.addCommentToPost(post, comment);
        comment.setPost(post);

        comment = commentRepository.save(comment);
        if (post.getAuthorId() != null) {
            PostCommentEvent postCommentEvent = buildPostCommentEvent(comment, post);
            redisMessagePublisher.publish(redisTopicProperties.getPostCommentChannel(), postCommentEvent);
        }
        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto updateComment(long commentId, String content) {
        log.info("Trying to update comment: {} with the following content: {}",
                commentId, content);
        Comment comment = getCommentById(commentId);
        comment.setContent(content);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public List<CommentDto> getPostComments(long postId) {
        log.info("Trying to get comments of post: {}", postId);
        return commentMapper.toDto(commentRepository.findAllByPostIdSortedByDate(postId));
    }

    @Transactional
    public void deleteComment(long commentId) {
        log.info("Trying to delete comment: {commentId}");
        commentRepository.deleteById(commentId);
    }


    public Comment getCommentById(long commentId) {
        log.debug("start searching comment by ID {}", commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment is not found"));
    }

    public boolean isCommentNotExist(long commentId) {
        log.debug("start searching for existence comment with id {}", commentId);
        return !commentRepository.existsById(commentId);
    }

    private void validateUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException ex) {
            throw new EntityNotFoundException("User does not exist");
        }
    }

    public void verifyComments() {
        List<Comment> notVerifiedComments = commentRepository.findByVerifiedIsNull();
        log.info("Starting moderation of {} comments", notVerifiedComments.size());

        Flux.fromIterable(notVerifiedComments)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(comment -> textAnalysisService.analyzeText(comment.getContent())
                        .publishOn(Schedulers.boundedElastic())
                        .doOnNext(response -> {
                            comment.setVerified(textAnalysisService.textAnalysisProcessing(response));
                            comment.setVerifiedDate(LocalDateTime.now());
                            commentRepository.save(comment);
                        })
                        .doOnError(e -> log.error("Error processing comment '{}'", comment, e))
                        .onErrorResume(e -> Mono.empty())
                ).sequential()
                .doOnComplete(() -> log.info("The comment moderation process has been completed"))
                .doOnError(e -> log.error("Error during overall comment processing: ", e))
                .subscribe();
    }

    private PostCommentEvent buildPostCommentEvent(Comment comment, Post post) {
        UserDto commentAuthor = userServiceClient.getUser(comment.getAuthorId());
        UserForNotificationDto userForNotification = userServiceClient.getUserForNotificationById(post.getAuthorId());

        return PostCommentEvent.builder()
                .receiverId(userForNotification.id())
                .commentAuthorUserName(commentAuthor.getUsername())
                .commentContent(comment.getContent())
                .build();
    }
}
