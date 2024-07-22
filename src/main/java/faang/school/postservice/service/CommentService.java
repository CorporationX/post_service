package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentMapper commentMapper;

    @Transactional
    public void createComment(CommentDto commentDto) {
        validateUserById(commentDto.getAuthorId());
        Post post = postService.getPostById(commentDto.getPostId());
        post.getComments().add(commentRepository
                .save(commentMapper.toEntity(commentDto)));
        postService.updatePost(post);
    }

    @Transactional
    public void updateComment(CommentDto commentDto) {
        Comment comment = getValidatedCommentById(commentDto.getId());
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllByPostId(Long postId) {
        Post post = postService.getPostById(postId);
        return commentMapper.toDtos(post.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment getValidatedCommentById(Long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> {
                    log.info("Comment not found, method getValidationCommentById");
                    return new EntityNotFoundException("Comment not found");
                });
    }

    private void validateUserById(Long userId) {
        if (userServiceClient.getUser(userId) == null)
            throw new RuntimeException("User not found");
    }
}
