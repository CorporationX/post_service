package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment findById(long id) {
        return commentRepository.findById(id).orElseThrow(() -> {
            log.info("Comment with id {} does not exist", id);
            return new EntityNotFoundException("Comment with id " + id + " does not exist");
        });
    }
}
