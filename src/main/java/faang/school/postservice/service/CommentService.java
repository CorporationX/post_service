package faang.school.postservice.service;

import faang.school.postservice.exсeption.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment getComment(long commentId){
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment with this Id does not exist !"));
    }
}
