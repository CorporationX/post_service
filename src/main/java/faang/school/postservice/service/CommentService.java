package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundEntityException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentValidator commentValidator;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public void delete(long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsByPostId(long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        commentValidator.checkPostIsExist(commentDto.getPostId());

        Comment savedComment = commentRepository.save(commentMapper.toEntity(commentDto));
        return commentMapper.toDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(UpdateCommentDto updateCommentDto) {
        Comment savedComment = commentRepository.findById(updateCommentDto.getId())
                .orElseThrow(() -> {
                    log.info("Couldn't find the saved comment into Comment Repository : updateComment");
                    throw new  EntityNotFoundException("Couldn't find saved comment in repository ID = " + updateCommentDto.getId());
                });

        savedComment.setContent(updateCommentDto.getContent());

        return commentMapper.toDto(commentRepository.save(savedComment));
    }

    @Transactional(readOnly = true)
    public Comment validationAndCommentsReceived(LikeDto likeDto) {
        if (likeDto.getCommentId() != null) {
            if (!commentRepository.existsById(likeDto.getCommentId())) {
                throw new DataValidationException("no such postId exists commentId: " + likeDto.getCommentId());
            }
        } else {
            throw new DataValidationException("arrived likeDto with postId and commentId equal to null");
        }
        return commentRepository.findById(likeDto.getCommentId()).orElseThrow(() ->
                new NotFoundEntityException("Not found comment by id: " + likeDto.getCommentId()));
    }
}
