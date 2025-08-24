package faang.school.postservice.facade.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentFacade {

    private final CommentMapper commentMapper;
    private final CommentService commentService;

    public CommentDtoResponse create(CommentCreateDto dto) {
        Comment comment = commentMapper.toEntityFromCreateDto(dto);
        log.debug("Mapping CommentCreateDto to Comment entity. DTO content: {}. Entity content: {}.",
                dto, comment);

        Comment createdComment = commentService.create(dto.getPostId(), comment);

        CommentDtoResponse commentResponseDto = commentMapper.toDto(createdComment);
        log.debug("Mapping Comment entity to CommentDto. Entity content: {}. DTO content: {}.",
                createdComment, commentResponseDto);

        return commentResponseDto;
    }

    public CommentDtoResponse update(CommentUpdateDto dto) {
        Comment existing = commentService.get(dto.getId());
        commentMapper.updateEntityFromDto(dto, existing);
        log.debug("Mapping CommentUpdateDto to Comment entity. DTO content: {}. Entity content: {}.",
                dto, existing);

        Comment updatedEvent = commentService.update(existing);

        CommentDtoResponse commentResponseDto = commentMapper.toDto(updatedEvent);
        log.debug("Mapping Comment entity to CommentDto. Entity content: {}. DTO content: {}.",
                updatedEvent, commentResponseDto);

        return commentResponseDto;
    }

    public CommentDtoResponse getCommentById(long commentId) {
        Comment comment = commentService.get(commentId);

        CommentDtoResponse commentResponseDto = commentMapper.toDto(comment);
        log.debug("Mapping Comment entity to CommentDto. Entity content: {}. DTO content: {}.",
                comment, commentResponseDto);

        return commentResponseDto;
    }

    public List<CommentDtoResponse> getAllByPostId(Long postId) {
        List<Comment> comments = commentService.getAllByPostId(postId);

        List<CommentDtoResponse> commentResponseDtoList = commentMapper.toDtoList(comments);
        log.debug("Mapping Comment entity list to CommentDto list. Entity content: {}. DTO content: {}.",
                comments, commentResponseDtoList);
        return commentResponseDtoList;
    }

    public void delete(long id) {
        commentService.delete(id);
    }
}
