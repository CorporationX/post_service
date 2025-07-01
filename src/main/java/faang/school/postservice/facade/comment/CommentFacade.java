package faang.school.postservice.facade.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
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

    public CommentDto create(CommentCreateDto dto) {
        Comment comment = commentMapper.toEntityFromCreateDto(dto);
        log.debug("Mapping CommentCreateDto to Comment entity. DTO content: {}. Entity content: {}.",
                dto, comment);

        Comment createdComment = commentService.create(dto.getPostId(), comment);

        CommentDto commentResponseDto = commentMapper.toDto(createdComment);
        log.debug("Mapping Comment entity to CommentDto. Entity content: {}. DTO content: {}.",
                createdComment, commentResponseDto);

        return commentResponseDto;
    }

    public CommentDto update(CommentUpdateDto dto) {
        Comment existing = commentService.get(dto.getId());
        commentMapper.updateEntityFromDto(dto, existing);
        log.debug("Mapping CommentUpdateDto to Comment entity. DTO content: {}. Entity content: {}.",
                dto, existing);

        Comment updatedEvent = commentService.update(existing);

        CommentDto commentResponseDto = commentMapper.toDto(updatedEvent);
        log.debug("Mapping Comment entity to CommentDto. Entity content: {}. DTO content: {}.",
                updatedEvent, commentResponseDto);

        return commentResponseDto;
    }

    public CommentDto getCommentById(long commentId) {
        Comment comment = commentService.get(commentId);

        CommentDto commentResponseDto = commentMapper.toDto(comment);
        log.debug("Mapping Comment entity to CommentDto. Entity content: {}. DTO content: {}.",
                comment, commentResponseDto);

        return commentResponseDto;
    }

    public List<CommentDto> getAllByPostId(Long postId) {
        List<Comment> comments = commentService.getAllByPostId(postId);

        List<CommentDto> commentResponseDtoList = commentMapper.toDtoList(comments);
        log.debug("Mapping Comment entity list to CommentDto list. Entity content: {}. DTO content: {}.",
                comments, commentResponseDtoList);
        return commentResponseDtoList;
    }

    public void delete(long id) {
        commentService.delete(id);
    }
}
