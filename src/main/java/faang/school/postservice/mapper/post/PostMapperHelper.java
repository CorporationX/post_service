package faang.school.postservice.mapper.post;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapperHelper {
    private final CommentRepository commentRepository;

    public List<Comment> getCommentsById(List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return new ArrayList<>();
        } else {
            return commentRepository.findAllById(commentIds);
        }
    }
}
