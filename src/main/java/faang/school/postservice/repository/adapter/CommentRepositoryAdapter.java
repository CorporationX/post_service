package faang.school.postservice.repository.adapter;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentRepositoryAdapter {
    private final CommentRepository commentRepository;

    public Comment getById(long id) {
        return commentRepository.findById(id).orElseThrow(() -> {
            log.error("There is no comment with ID {}", id);
            return new EntityNotFoundException("There is no comment with ID " + id);
        });
    }
}
