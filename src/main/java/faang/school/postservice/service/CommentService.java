package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    
    public CommentDto create(CommentDto commentDto){
        Comment comment = commentMapper.commentToEntity(commentDto);
        commentRepository.save(comment);
        return commentMapper.commentToDto(comment);
    }
}
