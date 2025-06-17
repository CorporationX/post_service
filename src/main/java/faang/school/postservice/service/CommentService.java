package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.Utils;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    public static final String COMMENT_BY_ID_NOT_FOUND = "Comment by id [{}] not found";

    private final CommentRepository commentRepository;
    private final Utils utils;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;

    /**
     * @return возвращает Comment по его id
     */
    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new CommentNotFoundException(utils.format(COMMENT_BY_ID_NOT_FOUND, commentId)));
    }

    @Transactional
    public CommentDto addComment(CommentDto commentDto) {
        validateComment(commentDto);
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        comment.setPost(post);
        comment.setAuthorId(commentDto.getAuthorId());
        comment.setCreatedAt(LocalDateTime.now());

        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        Comment existingComment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        existingComment.setContent(commentDto.getContent());

        existingComment = commentRepository.save(existingComment);
        return commentMapper.toDto(existingComment);
    }

    public List<CommentDto> getAllComments(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void validateComment(CommentDto commentDto) {
        if (commentDto == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }
        if (commentDto.getContent() == null || commentDto.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        if (commentDto.getContent().length() > 4096) {
            throw new IllegalArgumentException("Comment cannot be longer than 4096 characters");
        }
        if (commentDto.getAuthorId() == null) {
            throw new IllegalArgumentException("Comment must have an author");
        }
        if (commentDto.getPostId() == null) {
            throw new IllegalArgumentException("Comment must be associated with a post");
        }

        try {
            UserDto user = userServiceClient.getUser(commentDto.getAuthorId());
        } catch (FeignException e) {
            throw new IllegalArgumentException("Author not found");
        }

        postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }
}