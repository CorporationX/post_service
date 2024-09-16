package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto) {
        userServiceClient.getUser(commentDto.getAuthorId());
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id " + commentDto.getPostId()));

        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void updateContent(long commentId, String content) {
        commentRepository.updateContentById(commentId, content);
        commentRepository.updateUpdateDateById(commentId, LocalDateTime.now());
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.getByPostIdOrderByCreatedAtDesc(postId);
        return commentMapper.toDto(comments);
    }

    @Override
    @Transactional
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }
}