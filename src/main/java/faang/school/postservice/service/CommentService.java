package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional(rollbackFor = Exception.class)
    public Comment getComment(Long commentId) {
        log.info("Start method getComment with commentId: {}", commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment not found!"));
    }
}
