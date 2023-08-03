package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto create(CommentDto commentDto){
        Comment comment = commentMapper.commentToEntity(commentDto);
        commentRepository.save(comment);

        return commentMapper.commentToDto(comment);
    }

    @Transactional
    public CommentDto update(CommentDto commentDto){
        Optional<Comment> comment = Optional.ofNullable(commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new DataValidationException(
                        MessageFormat.format(ErrorMessage.COMMENT_NOT_FOUND_FORMAT, commentDto.getId()))));

        comment.get().setContent(commentDto.getContent());
        comment.get().setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment.get());

        return commentMapper.commentToDto(comment.get());
    }

    @Transactional
    public void delete(Long id){
        Comment comment = commentRepository.findById(id)
                        .orElseThrow(()-> new EntityNotFoundException(
                                MessageFormat.format(ErrorMessage.COMMENT_NOT_FOUND_FORMAT, id)));

        commentRepository.delete(comment);
    }

    public List<CommentDto> getCommentsForPost(Long postId){
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::commentToDto)
                .toList();
    }
}