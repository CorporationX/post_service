package faang.school.postservice.facade.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentMapper commentMapper;
    private final CommentService commentService;

    public CommentDto create(CommentCreateDto dto) {
        Comment comment = commentMapper.toEntityFromCreateDto(dto);
        Comment createdComment = commentService.create(dto.getPostId(), comment);

        return commentMapper.toDto(createdComment);
    }

    public CommentDto update(CommentUpdateDto dto) {
        Comment existing = commentService.get(dto.getId());
        commentMapper.updateEntityFromDto(dto, existing);
        Comment updatedEvent = commentService.update(existing);

        return commentMapper.toDto(updatedEvent);
    }

    public List<CommentDto> getAllByPostId(Long postId) {
        List<Comment> comments = commentService.getAllByPostId(postId);
        return commentMapper.toDtoList(comments);
    }

    public void delete(long id) {
        commentService.delete(id);
    }
}
