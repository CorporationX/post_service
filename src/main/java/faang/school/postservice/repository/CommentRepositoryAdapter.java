package faang.school.postservice.repository;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentRepositoryAdapter {
    private final CommentRepository commentRepository;

    public Comment findById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException(String.format("Комментарий с id:%s не найден!", commentId)));
    }
}
