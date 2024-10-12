package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getUnverifiedComments() {
        return commentRepository.findByVerifiedAtIsNull();
    }

    public void saveComments(List<Comment> comments) {
        log.info("Trying to save comments in db");
        commentRepository.saveAll(comments);
        log.info("Comments saved");
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }
}
