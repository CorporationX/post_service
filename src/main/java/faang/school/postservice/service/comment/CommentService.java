package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void createComment(CommentDto commentDto){

    }

    public void updateComment(CommentDto commentDto){

    }

    public List<CommentDto> getAllComments(long postId){
        return null;
    }

    public void deleteComment(long commentId, long authorId){

    }
}
