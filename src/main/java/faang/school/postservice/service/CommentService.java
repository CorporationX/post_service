package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEditDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.moderator.CommentModerationDictionary;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Setter
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final PostValidator postValidator;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final CommentModerationDictionary commentModerationDictionary;
    private final CommentEventPublisher commentEventPublisher;

    @Value("${scheduler.moderation.comment.batch_size}")
    private int commentBatchSize;

    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        if (!userServiceClient.isUserExists(userContext.getUserId())) {
            throw new DataValidationException("User does not exist");
        }
        var comment = commentMapper.toEntity(commentDto);
        var post = postService.getPostById(postId);
        comment.setAuthorId(userContext.getUserId());
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        commentEventPublisher.publish(commentMapper.toEventDto(savedComment));
        return commentMapper.toDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentEditDto commentEditDto) {
        Comment commentToUpdate = getComment(commentId);
        postValidator.validateAuthor(commentToUpdate.getAuthorId(), userContext.getUserId());
        commentToUpdate.setContent(commentEditDto.getContent());
        commentRepository.save(commentToUpdate);
        return commentMapper.toDto(commentToUpdate);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return commentMapper.toDtoList(comments.stream()
                .sorted((x, y) -> y.getCreatedAt().compareTo(x.getCreatedAt()))
                .toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        postValidator.validateAuthor(comment.getAuthorId(), userContext.getUserId());
        commentRepository.delete(comment);
    }

    @Async("executorService")
    @Transactional
    public void moderateComment() {
        List<Comment> unverifiedComments = commentRepository.findAllCommentsByNotVerified();
        List<List<Comment>> commentSubLists = ListUtils.partition(unverifiedComments, commentBatchSize);
        log.info("Starting moderation for {} comments", unverifiedComments.size());
        for (List<Comment> subList : commentSubLists) {
                subList.forEach(comment -> {
                    giveStatusToComment(comment, !commentModerationDictionary.checkCommentForInsults(comment.getContent()));
                });
            }
        log.info("Moderation for {} comments finished", unverifiedComments.size());
    }

    private void giveStatusToComment(Comment comment, boolean verified) {
        comment.setVerificationDate(LocalDateTime.now());
        comment.setVerified(verified);
        commentRepository.save(comment);
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment has not been found"));
    }
}

