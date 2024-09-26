package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SortingField;
import faang.school.postservice.dto.comment.SortingOrder;
import faang.school.postservice.dto.comment.SortingStrategyDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.sort.CommentSortingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static faang.school.postservice.exception.ExceptionMessages.COMMENT_NOT_FOUND;
import static faang.school.postservice.exception.ExceptionMessages.POST_DELETED_OR_NOT_PUBLISHED;
import static faang.school.postservice.exception.ExceptionMessages.POST_NOT_FOUND;
import static faang.school.postservice.exception.ExceptionMessages.WRONG_AUTHOR_ID;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userClientService;
    private final UserContext userContext;
    private final CommentMapper commentMapper;
    private final Map<SortingOrder, Map<SortingField, CommentSortingStrategy>> sortingStrategiesAppliers;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserServiceClient userClientService,
                              UserContext userContext,
                              CommentMapper commentMapper,
                              List<CommentSortingStrategy> sortingStrategies) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userClientService = userClientService;
        this.userContext = userContext;
        this.commentMapper = commentMapper;
        this.sortingStrategiesAppliers = sortingStrategies.stream()
                .collect(Collectors.groupingBy(
                        CommentSortingStrategy::getOrder,
                        Collectors.toMap(CommentSortingStrategy::getField, Function.identity())));
    }

    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Post post = getPost(postId);
        validateAuthor(commentDto.authorId());
        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        log.info("Saved comment: {}, for post: {}", savedComment.getId(), post.getId());
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = getComment(commentId);
        validateAuthor(comment.getAuthorId());
        if (comment.getPost().isDeleted()) {
            throw new DataValidationException(
                    POST_DELETED_OR_NOT_PUBLISHED.getMessage().formatted(comment.getPost().getId()));
        }
        comment.setContent(commentDto.content());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Updated comment: {}, for post: {}", updatedComment.getId(), comment.getPost().getId());
        return commentMapper.toCommentDto(updatedComment);
    }

    @Override
    public List<CommentDto> getComments(Long projectId, SortingStrategyDto sortingStrategy) {
        Post post = getPost(projectId);
        CommentSortingStrategy sortingApplier = sortingStrategiesAppliers
                .get(sortingStrategy.order())
                .get(sortingStrategy.field());
        List<Comment> sortedComments = sortingApplier.getSortedComments(post.getComments());
        log.debug("Find {} comments for post: {}", sortedComments.size(), post.getId());
        return commentMapper.toCommentDtos(sortedComments);
    }

    @Override
    public CommentDto deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        validateAuthor(comment.getAuthorId());
        if (comment.getPost().isDeleted()) {
            throw new DataValidationException(
                    POST_DELETED_OR_NOT_PUBLISHED.getMessage().formatted(comment.getPost().getId()));
        }
        commentRepository.delete(comment);
        log.info("Deleted comment: {}", comment.getId());
        return commentMapper.toCommentDto(comment);
    }

    // в задании написано через PostService, потом переделаю как арстана смерджат
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
        if (authorId != userContext.getUserId()) {
            throw new DataValidationException(
                    WRONG_AUTHOR_ID.getMessage().formatted(authorId, userContext.getUserId()));
        }
    }
}
