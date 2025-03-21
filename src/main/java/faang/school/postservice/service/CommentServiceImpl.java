package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        if (commentDto == null) {
            throw new CommentValidationException("CommentDto cannot be null");
        }

        if (postId == null) {
            throw new CommentValidationException("Post ID cannot be null");
        }

        if (commentDto.authorId() == null) {
            throw new CommentValidationException("Author ID cannot be null");
        }

        if (commentDto.content() == null) {
            throw new CommentValidationException("Content cannot be null");
        }

        Post post = postService.findById(postId);

        userServiceClient.getUser(commentDto.authorId());

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        log.info("Created comment with id: {} for post with id: {}. Author: {}",
                savedComment.getId(), postId, savedComment.getAuthorId());
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        if (commentDto == null) {
            throw new CommentValidationException("CommentDto cannot be null");
        }

        Comment comment = validateCommentAndPost(postId, commentId);

        if (!comment.getAuthorId().equals(commentDto.authorId())) {
            throw new CommentValidationException(
                    String.format("User with id %d is not the author of the comment with id %d",
                            commentDto.authorId(), comment.getId())
            );
        }

        comment.setContent(commentDto.content());
        comment.setLargeImageFileKey(commentDto.largeImageFileKey());
        comment.setSmallImageFileKey(commentDto.smallImageFileKey());

        Comment updatedComment = commentRepository.save(comment);
        log.info("Updated comment with id: {} for post with id: {}", commentId, postId);
        return commentMapper.toDto(updatedComment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = validateCommentAndPost(postId, commentId);
        commentRepository.deleteById(commentId);
        log.info("Deleted comment with id: {} for post with id: {}. Author: {}",
                commentId, postId, comment.getAuthorId());
    }

    public Comment validateCommentAndPost(Long postId, Long commentId) {
        if (postId == null || commentId == null) {
            throw new CommentValidationException("Post ID and Comment ID cannot be null");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        if (comment.getPost() == null) {
            throw new CommentValidationException("Comment does not have a post assigned");
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new CommentValidationException("Comment does not belong to the specified post");
        }

        return comment;
    }
}