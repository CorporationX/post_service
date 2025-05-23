package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        log.info("Начало создания комментария: %s".formatted(commentDto.getContent()));
        Comment comment = commentMapper.toEntity(commentDto);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateCommentContent(long id, CommentDto commentDto) {
        log.info("Начало обновления комментария к посту c ID: %d".formatted(commentDto.getPostId()));
        Comment comment = findCommentById(id);
        commentMapper.updateCommentContent(comment, commentDto);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getAllComments(CommentDto commentDto) {
        log.info("Получаем все комментарии по ID поста: %d".formatted(commentDto.getPostId()));
        List<Comment> comments = commentRepository.findAllByPostId(commentDto.getPostId());
        return commentMapper.toCommentDtoList(comments);
    }

    @Override
    public void deleteComment(long id) {
        log.info("Удаляем комментарий с ID=%d ".formatted(id));
        commentRepository.delete(findCommentById(id));
    }

    private Comment findCommentById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Комментария с ID=%d не существует".formatted(id)));
    }
}
