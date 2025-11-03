package faang.school.postservice.controller.facade.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentService commentService;
    private final UserContext userContext;

    public CommentDto create(CommentCreateDto commentCreateDto) {
        Long userId = getCurrentUserId();
        Comment comment = commentService.create(commentCreateDto, userId);
        log.info("Creating comment for postId={} by userId={}", commentCreateDto.postId(), userId);
        return CommentMapper.toDto(comment);
    }

    public CommentDto update(Long commentId, CommentUpdateDto commentUpdateDto) {
        Long userId = getCurrentUserId();
        Comment comment = commentService.update(commentId, commentUpdateDto, userId);
        log.info("Updating comment id={} by userId={}", commentId, userId);
        return CommentMapper.toDto(comment);
    }

    public void delete(Long commentId) {
        Long userId = getCurrentUserId();
        commentService.delete(commentId, userId);
        log.info("Deleting comment id={} by userId={}", commentId, userId);
    }

    public CommentDto getById(Long commentId) {
        Comment comment = commentService.getById(commentId);
        log.info("Getting comment by id={}", commentId);
        return CommentMapper.toDto(comment);
    }

    public PageResponse<CommentDto> getByPostId(Long postId, Pageable pageable) {
        log.info("Fetching paged comments for postId={} with page={}", postId, pageable.getPageNumber());
        return commentService.findAllByPostId(postId, pageable);
    }

    private Long getCurrentUserId() {
        return userContext.getUserId();
    }
}
