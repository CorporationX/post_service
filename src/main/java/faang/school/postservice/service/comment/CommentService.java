package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public void createComment(CommentDto commentDto) {
        if (commentDto.getCreatedAt()==null){
            commentDto.setCreatedAt(LocalDateTime.now());
        }
        commentRepository.save(commentMapper.toEntity(commentDto));
    }

    public void updateComment(CommentDto commentDto) {

    }

    public List<CommentDto> getAllComments(long postId) {
        return null;
    }

    public void deleteComment(long commentId, long authorId) {

    }
}
