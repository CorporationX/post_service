package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository; // заменить на PostService postService;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional(readOnly = true)
    public List<ResponseCommentDto> getAllComments(long postId) {
        // добавить postService.getPostEntityById(postId);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        comments.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        log.info("Received {} comments for post with id: {}", comments.size(), postId);
        return comments.stream()
                .map(commentMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ResponseCommentDto createComment(long postId, CreateCommentDto createCommentDto) {
        validateCommentContent(createCommentDto.content());

        Post post = postRepository.findById(postId) //заменить на = postService.getPostEntityById(postId);
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));

        try {
            userServiceClient.getUser(createCommentDto.authorId());
        } catch (FeignException e) {
            log.warn("User with id {} not found in user_service", createCommentDto.authorId());
            throw new IllegalArgumentException("Author not found with id: " + createCommentDto.authorId());
        }

        Comment comment = commentMapper.toEntity(createCommentDto);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        log.info("Created comment with id: {} for post with id: {}", savedComment.getId(), postId);

        return commentMapper.toResponseDto(savedComment);
    }

    @Override
    @Transactional
    public ResponseCommentDto updateComment(long postId, long commentId, UpdateCommentDto updateCommentDto) {
        validateCommentContent(updateCommentDto.content());

        Comment newComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        if (!newComment.getPost().getId().equals(postId)) {
            log.warn("Comment {} does not belong to post {}", commentId, postId);
            throw new IllegalArgumentException("Comment does not belong to post with id: " + postId);
        }

        commentMapper.updateEntity(updateCommentDto, newComment);
        Comment updatedComment = commentRepository.save(newComment);
        log.info("Updated comment with id: {}", commentId);

        return commentMapper.toResponseDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(long postId, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        if (!comment.getPost().getId().equals(postId)) {
            log.warn("Comment {} does not belong to post {}", commentId, postId);
            throw new IllegalArgumentException("Comment does not belong to post with id: " + postId);
        }

        commentRepository.deleteById(commentId);
        log.info("Deleted comment with id: {}", commentId);
    }

    private void validateCommentContent(String content) {
        if (content == null || content.isBlank()) {
            log.warn("Comment content cannot be null or empty");
            throw new IllegalArgumentException("Comment content cannot be blank");
        }

        if (content.length() > 4096) {
            log.warn("Comment content exceeds maximum length of 4096 characters");
            throw new IllegalArgumentException("Comment content must not exceed 4096 characters");
        }
    }
}