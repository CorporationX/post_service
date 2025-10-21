package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.SendCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserContext userContext;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;


    @Transactional
    @Override
    public void sendComment(SendCommentDto sendCommentDto) {
        validateAuthorComment(sendCommentDto.authorId());
        Post post = postRepository.findById(sendCommentDto.postId())
                .orElseThrow(() -> {
                    log.error("The requested post by {} id was not found", sendCommentDto.postId());
                    return new EntityNotFoundException("No entity found for the specified %d id!"
                            .formatted(sendCommentDto.postId()));
                });
        Comment newComment = commentMapper.toEntity(sendCommentDto);
        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setPost(post);
        commentRepository.save(newComment);
    }

    @Transactional
    @Override
    public void updateComment(UpdateCommentDto updateCommentDto, long postId) {
        validateAuthorComment(updateCommentDto.authorId());
        Comment comment = getCommentByIdOrThrow(updateCommentDto.commentId());
        validateSameCommentOnPost(comment, postId);
        comment.setContent(updateCommentDto.content());
        comment.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    public List<ResponseCommentDto> getComments(long postId, int page, int pageSize) {
        if (!postRepository.existsById(postId)) {
            log.error("Calling a non-existent post {} -id, {} - user id", postId, userContext.getUserId());
            throw new EntityNotFoundException("This post for %d - id does not exist!".formatted(postId));
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = postRepository.findAllCommentByPostId(postId, pageable);
        return commentPage.map(commentMapper::toDto).getContent();
    }

    @Transactional
    @Override
    public void deleteComment(long commentId, long postId) {
        Comment comment = getCommentByIdOrThrow(commentId);
        validateSameCommentOnPost(comment, postId);
        commentRepository.delete(comment);
    }


    private Comment getCommentByIdOrThrow(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("The requested comment by {} id was not found", commentId);
                    return new EntityNotFoundException("No entity found for the specified %d id!"
                            .formatted(commentId));
                });
    }

    private void validateSameCommentOnPost(Comment comment, long postId) {
        if (!Objects.equals(comment.getPost().getId(), postId)) {
            log.error("User {} - id is trying to update a comment from another {} - post id. {} - Passed comment id",
                    userContext.getUserId(), postId, comment.getId());
            throw new ForbiddenException("You cannot edit comments on another post %d - Id of the post with your comment"
                    .formatted(postId));
        }
    }

    private void validateAuthorComment(long authorId) {
        long contextId = userContext.getUserId();
        if (!Objects.equals(contextId, authorId)) {
            log.warn("The user is trying to change someone else's data, {} - the user, {} - the Original user",
                    contextId, authorId);
            throw new ForbiddenException("You cannot change other people's data!");
        }
    }
}