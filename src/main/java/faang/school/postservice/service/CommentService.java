package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.*;
import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        postService.getPostById(postId);
        return commentMapper.toDtoList(
                commentRepository.findAllByPostId(postId).stream()
                        .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                        .toList()
        );
    }

    @Transactional
    public CommentDto createComment(Long postId, CommentCreateDto dto, Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            throw new CommentValidationException("User with id = " + authorId + " was not found");
        }

        Post post = postService.getPostById(postId);
        Comment comment = commentMapper.toEntityFromCreateDto(dto);
        comment.setPost(post);
        comment.setAuthorId(authorId);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentUpdateDto dto, Long userId) {
        Comment comment = getCommentById(commentId);
        commentValidator.validateAuthor(comment, userId);
        commentMapper.updateEntityFromDto(dto, comment);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);
        commentValidator.validateAuthor(comment, userId);
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentValidationException("Comment with id = " + id + " was not found"));
    }
}