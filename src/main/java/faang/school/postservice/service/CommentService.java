package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publishers.CommentPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validation.CommentValidation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final CommentValidation commentValidation;
    private final CommentPublisher commentPublisher;

    public CommentDto create(CommentDto commentDto, long userId) {
        commentValidation.authorExistenceValidation(userId);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setLikes(Collections.EMPTY_LIST);
        comment.setPost(postService.getPost(commentDto.getPostId()));
        Comment newComment = commentRepository.save(comment);
        CommentEvent commentEvent = CommentEvent.builder()
                .commentId(newComment.getId())
                .authorId(newComment.getAuthorId())
                .content(newComment.getContent())
                .postId(newComment.getPost().getId())
                .build();
        commentPublisher.publish(commentEvent);
        return commentMapper.toDto(newComment);
    }

    public CommentDto update(CommentDto commentDto, long userId) {
        commentValidation.authorExistenceValidation(userId);

        commentValidation.validateCommentExistence(commentDto.getId());

        Comment comment = commentRepository.findById(commentDto.getId()).get();
        comment.setContent(commentDto.getContent());

        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getPostComments(Long postId) {
        Post post = postService.getPost(postId);
        List<Comment> comments = post.getComments();
        return commentMapper.toDto(comments);
    }

    public void delete(CommentDto commentDto, Long userId) {
        commentValidation.authorExistenceValidation(userId);
        commentValidation.validateCommentExistence(commentDto.getId());
        commentRepository.deleteById(commentDto.getId());
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment by id: " + commentId + " not found"));
    }
}
