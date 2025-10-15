package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentDto save(CommentDto commentDto) {
        Comment comment = commentMapper.toComment(commentDto);
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);

        //1. Комментарии могут оставлять только конкретные пользователи под любыми постами.
        //2. Комментарий можно создать. Он не может быть пустым и длиннее 4096 символов.
        // У него обязательно должен быть автор — существующий пользователь.
        // Комментарий обязательно должен относиться к посту. Также у комментария есть дата создания.

        // Не совсем понял как реализовать
    }

    public List<CommentDto> findAllByPostId(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    public CommentDto findById(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        return commentMapper.toDto(comment);
    }

    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
