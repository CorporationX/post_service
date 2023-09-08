package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentDto getCommentById(Long commentId) {
        return commentMapper.toCommentDto(commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found")));
    }
}
