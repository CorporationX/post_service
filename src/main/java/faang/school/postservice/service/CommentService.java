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
        postService.getPostById(postId); // Проверка, что пост существует
        return commentMapper.toDtoList(
                commentRepository.findAllByPostId(postId).stream()
                        .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                        .toList()
        );
    }

    @Transactional
    public CommentDto createComment(CommentCreateDto dto, Long authorId) {
        validateUserExists(authorId);

        Post post = postService.getPostById(dto.postId());
        Comment comment = commentMapper.toEntityFromCreateDto(dto);

        comment.setAuthorId(authorId);
        comment.setPost(post);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(CommentUpdateDto dto, Long userId) {
        Comment comment = getCommentById(dto.commentId());

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

    private void validateUserExists(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new CommentValidationException("User with id = " + userId + " was not found");
        }
    }
}
