package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;

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

    public CommentDto update(CommentDto commentDto){
        Optional<Comment> comment = commentRepository.findById(commentDto.getId());
        if (comment.isEmpty()){
            throw new DataValidationException(
                    MessageFormat.format(ErrorMessage.COMMENT_NOT_FOUND_FORMAT, commentDto.getId()));
        }

        comment.get().setContent(commentDto.getContent());
        comment.get().setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment.get());

        return commentMapper.commentToDto(comment.get());
    }
}