package faang.school.postservice.service;

import faang.school.postservice.exception.DataLikeValidation;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new DataLikeValidation("Комментария с id " + commentId + " нет в базе данных."));
    }
}