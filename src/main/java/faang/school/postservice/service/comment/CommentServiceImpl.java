package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SortingStrategyDto;
import faang.school.postservice.dto.redis.event.CommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.MessagePublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.sort.CommentSortingStrategy;
import faang.school.postservice.service.comment.sort.SortingStrategyAppliersMap;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.postservice.exception.comment.ExceptionMessages.COMMENT_NOT_FOUND;
import static faang.school.postservice.exception.comment.ExceptionMessages.POST_DELETED_OR_NOT_PUBLISHED;
import static faang.school.postservice.exception.comment.ExceptionMessages.POST_NOT_FOUND;
import static faang.school.postservice.exception.comment.ExceptionMessages.WRONG_AUTHOR_ID;
import static faang.school.postservice.exception.comment.ExceptionMessages.WRONG_POST_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userClientService;
    private final UserContext userContext;
    private final CommentMapper commentMapper;
    private final SortingStrategyAppliersMap sortingStrategiesAppliers;
    private final CommentChecker commentChecker;
    private final MessagePublisher<Long> banUserPublisher;
    private final MessagePublisher<CommentEvent> commentPublisher;

    @Value("${comment.constants.verification-days-limit}")
    private int verificationDaysLimit;

    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Post post = getPost(postId);
        //validateAuthor(commentDto.authorId());
        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);
        commentRepository.save(comment);
        log.info("Saved comment: {}, for post: {}", comment.getId(), post.getId());

        CommentEvent event = commentMapper.toCommentEvent(comment);
        commentPublisher.publish(event);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        Comment comment = getComment(commentId);
        validateAuthor(comment.getAuthorId());
        validatePostIdForComment(postId, comment);
        if (comment.getPost().isDeleted()) {
            throw new DataValidationException(
                    POST_DELETED_OR_NOT_PUBLISHED.getMessage().formatted(comment.getPost().getId()));
        }
        comment.setContent(commentDto.content());
        commentRepository.save(comment);
        log.info("Updated comment: {}, for post: {}", comment.getId(), comment.getPost().getId());
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getComments(Long projectId, SortingStrategyDto sortingStrategy) {
        Post post = getPost(projectId);
        CommentSortingStrategy sortingApplier = sortingStrategiesAppliers.getStrategy(
                sortingStrategy.order(),
                sortingStrategy.field());
        List<Comment> sortedComments = sortingApplier.apply(post.getComments());
        log.debug("Find {} comments for post: {}", sortedComments.size(), post.getId());
        return commentMapper.toCommentDtos(sortedComments);
    }

    @Override
    public CommentDto deleteComment(Long postId, Long commentId) {
        Comment comment = getComment(commentId);
        validateAuthor(comment.getAuthorId());
        validatePostIdForComment(postId, comment);
        if (comment.getPost().isDeleted()) {
            throw new DataValidationException(
                    POST_DELETED_OR_NOT_PUBLISHED.getMessage().formatted(comment.getPost().getId()));
        }
        commentRepository.delete(comment);
        log.info("Deleted comment: {}", comment.getId());
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<Comment> getUnverifiedComments() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(verificationDaysLimit);
        return commentRepository.findUnverifiedComments(startDate);
    }

    @Async("taskExecutor")
    @Override
    public void verifyComments(List<Comment> comments) {
        comments.forEach(comment -> {
            comment.setVerified(commentChecker.isAcceptableComment(comment));
            comment.setVerifiedDate(LocalDateTime.now());
        });
        commentRepository.saveAll(comments);
        log.info("Verified comments: {}", comments.size());
    }

    @Override
    public void banUsersWithObsceneCommentsMoreThan(int banCommentLimit) {
        List<Long> usersIds = commentRepository.findUserIdsToBan(banCommentLimit);
        log.info("Found {} users to Ban", usersIds.size());
        usersIds.forEach(banUserPublisher::publish);
    }

    private Post getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException(POST_NOT_FOUND.getMessage().formatted(postId)));
        if (post.isDeleted() || !post.isPublished()) {
            throw new DataValidationException(POST_DELETED_OR_NOT_PUBLISHED.getMessage().formatted(postId));
        }
        return post;
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(COMMENT_NOT_FOUND.getMessage().formatted(commentId)));
    }

    private void validateAuthor(Long authorId) {
        UserDto authorDto = userClientService.getUser(authorId);
        Long userId = userContext.getUserId();
        if (authorId.equals(userId) == false) {
            throw new DataValidationException(
                    WRONG_AUTHOR_ID.getMessage().formatted(userId, authorId));
        }
    }

    private void validatePostIdForComment(Long postId, Comment comment) {
        if (comment.getPost().getId() != postId) {
            throw new DataValidationException(WRONG_POST_ID.getMessage().formatted(postId, comment.getPost().getId()));
        }
    }
}
