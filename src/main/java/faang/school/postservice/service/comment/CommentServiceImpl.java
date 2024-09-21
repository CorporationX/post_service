package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto) {
        // Завязан на реализации Владислава, его задача на ревью
        try {
            userServiceClient.getUser(commentDto.getAuthorId());
        } catch (FeignException e) {
            throw new EntityNotFoundException("User not found");
        }

        Post post = postRepository
                .findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with ID %s not found", commentDto.getPostId())));

        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void updateComment(long commentId, String content) {
        commentRepository.updateContentById(commentId, content);
        commentRepository.updateDateById(commentId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.getByPostIdOrderByCreatedAtDesc(postId);
        return commentMapper.toDto(comments);
    }

    @Override
    @Transactional
    public boolean deleteComment(long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }
}
