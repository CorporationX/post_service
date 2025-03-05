package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.event.NewCommentEvent;
import faang.school.postservice.kafka.producer.KafkaCommentProducer;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.comment.CommentIdValidator;
import faang.school.postservice.validator.comment.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final CommentIdValidator commentIdValidator;
    private final CommentValidator commentValidator;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserContext userContext;
    private final ModerationDictionary moderator;
    private final KafkaCommentProducer kafkaCommentProducer;

    public ResponseCommentDto addComment(Long postId, CommentDto commentDto) {
        validateUser(commentDto.getAuthorId());
        log.info("User {} validated successfully", commentDto.getAuthorId());
        log.info("Starting to create comment for post: {}", postId);

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userContext.getUserId());
        comment.setPost(getPost(postId));

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment creation completed for post: {}, comment id: {}", postId, savedComment.getId());

        kafkaCommentProducer.sendCommentEvent(NewCommentEvent.builder()
                .id(savedComment.getId())
                .postId(postId)
                .authorId(savedComment.getAuthorId())
                .content(savedComment.getContent())
                .createdAt(savedComment.getCreatedAt())
                .build());

        ResponseCommentDto responseDto = ResponseCommentDto.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .authorId(savedComment.getAuthorId())
                .postId(savedComment.getPost().getId())
                .createdAt(savedComment.getCreatedAt())
                .updatedAt(savedComment.getUpdatedAt())
                .likeIds(new ArrayList<>())
                .build();

        log.info("Prepared response DTO for comment: {}", responseDto.getId());
        return responseDto;
    }

    public ResponseCommentDto updateComment(CommentDto receivedCommentDto) {
        Comment actualComment = getComment(receivedCommentDto.getId());
        commentValidator.validComment(actualComment, receivedCommentDto);
        actualComment.setContent(receivedCommentDto.getContent());
        actualComment = commentRepository.save(actualComment);
        return commentMapper.toResponseDto(actualComment);
    }

    public List<ResponseCommentDto> getCommentsByPostId(Long postId) {
        Post post = getPost(postId);
        commentValidator.validPostComments(post);
        List<Comment> comments = post.getComments();
        return comments.stream()
                .sorted((comment1, comment2) -> comment1.getCreatedAt().compareTo(comment2.getCreatedAt()))
                .map(commentMapper::toResponseDto)
                .toList();
    }

    public List<Long> getCommentIdsByPostId(Long postId) {
        Post post = getPost(postId);
        commentValidator.validPostComments(post);
        if (!post.isPublished()) {
            log.warn("Attempted to get comments from unpublished post: {}", postId);
            return Collections.emptyList();
        }
        List<Comment> comments = post.getComments();
        log.debug("Found {} comments for published post: {}", comments.size(), postId);
        return comments.stream()
                .filter(Comment::getVerified)
                .map(Comment::getId)
                .toList();
    }

    public void deleteComment(Long commentId) {
        existsComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private void validateUser(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (userDto == null) {
            throw new EntityNotFoundException(String.format("Юзера с id %d не существует!", userId));
        }
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Такого поста не существует"));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("Комментарий не найден"));
    }

    private void existsComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Такого комментария не существует");
        }
    }

    public Comment findCommentById(Long commentId) {
        commentIdValidator.validateCommentId(commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    public boolean isExits(Long commentId) {
        commentIdValidator.validateCommentId(commentId);
        return commentRepository.existsById(commentId);
    }

    @Transactional
    public void verifyComments(int subListSize) {
        List<Comment> comments = commentRepository.findAllUnCheckedComments();
        if (comments.isEmpty()) {
            log.info("No comments for moderation");
            return;
        }
        log.info("Found {} comments for moderation", comments.size());
        List<List<Comment>> partitionsComments = ListUtils.partition(comments, subListSize);
        for (List<Comment> partitionComments : partitionsComments) {
            verifyComment(partitionComments);
        }
        log.info("Verified all {} comments", comments.size());
    }

    @Async("executorCommentModerator")
    protected void verifyComment(List<Comment> comments) {
        CompletableFuture.runAsync(() -> {
            comments.forEach(comment -> {
                comment.setVerified(moderator.checkCurseWordsInComment(comment.getContent()));
                comment.setVerifiedAt(LocalDateTime.now());
            });
            commentRepository.saveAll(comments);
            log.info("Verified {} comments", comments.size());
        });
    }

    public List<Comment> getLatestCommentsByPostId(Long postId, int limit) {
        try {
            Post post = getPost(postId);
            if (!post.isPublished()) {
                log.warn("Attempted to get comments from unpublished post: {}", postId);
                return Collections.emptyList();
            }

            return commentRepository
                    .findLatestByPostId(postId, PageRequest.of(0, limit))
                    .stream()
                    .filter(Comment::isVerified)
                    .toList();
        } catch (Exception e) {
            log.error("Error getting latest comments for post {}", postId, e);
            return Collections.emptyList();
        }
    }
}