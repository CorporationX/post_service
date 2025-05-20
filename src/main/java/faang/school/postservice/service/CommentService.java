package faang.school.postservice.service;

import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    public static final String COMMENT_BY_ID_NOT_FOUND = "Comment by id [{}] not found";

    private final CommentRepository commentRepository;
    private final Utils utils;

    /**
     * @return возвращает Comment по его id
     */
    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new CommentNotFoundException(utils.format(COMMENT_BY_ID_NOT_FOUND, commentId)));
    }
}
