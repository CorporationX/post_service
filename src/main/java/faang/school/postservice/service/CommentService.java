package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.CommentValidator;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserContext userContext;
    private final CommentValidator commentValidator;
    private final PostRepository postRepository;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        commentValidator.existUser(userContext.getUserId());
        commentDto.setAuthorId(userContext.getUserId());
        commentValidator.existPost(commentDto.getPostId());
        Comment comment = commentMapper.dtoToEntity(commentDto);
        comment.setPost(postRepository.findById(commentDto.getPostId()).get());
        return commentMapper.entityToDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        commentValidator.existComment(commentDto.getId());
        Comment comment = commentRepository.findById(commentDto.getId()).orElseThrow();
        commentValidator.checkUserIsAuthorComment(comment, userContext.getUserId());
        comment.setContent(commentDto.getContent());
        return commentMapper.entityToDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(long commentId) {
        commentValidator.existComment(commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        commentValidator.checkUserIsAuthorComment(comment, userContext.getUserId());
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> findAllByPostId(long postId) {
        commentValidator.existPost(postId);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        if (comments.isEmpty()) {
            String msg = "Post with id:%d has no comments";
            log.error(String.format(msg, postId));
            throw new EntityNotFoundException(String.format(msg, postId));
        }
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::entityToDto)
                .collect(Collectors.toList());
    }
}
