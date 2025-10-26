package faang.school.postservice.controller.comment.facade;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentService commentService;
    private final UserContext userContext;

    public CommentDto create(CommentCreateDto commentCreateDto) {
        Long userId = getCurrentUserId();
        log.info("Creating comment for postId={} by userId={}", commentCreateDto.postId(), userId);
        Comment comment = commentService.create(commentCreateDto, userId);
        return CommentMapper.toDto(comment);
    }

    public CommentDto update(Long commentId, CommentUpdateDto commentUpdateDto) {
        Long userId = getCurrentUserId();
        log.info("Updating comment id={} by userId={}", commentId, userId);
        Comment comment = commentService.update(commentId, commentUpdateDto, userId);
        return CommentMapper.toDto(comment);
    }

    public void delete(Long commentId) {
        Long userId = getCurrentUserId();
        log.info("Deleting comment id={} by userId={}", commentId, userId);
        commentService.delete(commentId, userId);
    }

    public CommentDto getById(Long commentId) {
        log.info("Getting comment by id={}", commentId);
        Comment comment = commentService.getById(commentId);
        return CommentMapper.toDto(comment);
    }

    public List<CommentDto> getByPostId(Long postId) {
        log.info("Getting comments for postId={}", postId);
        return commentService.getByPostId(postId);
    }

    private Long getCurrentUserId() {
        return userContext.getUserId();
    }
}
