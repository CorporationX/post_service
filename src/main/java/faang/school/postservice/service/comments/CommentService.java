package faang.school.postservice.service.comments;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        try {
            userServiceClient.getUser(commentDto.getAuthorId());
        } catch (Exception e) {
            throw new IllegalArgumentException("No author found");
        }

        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("No post found"));

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed()) // from oldest to newest
                .map(commentMapper::toDto)
                .toList();
    }

    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("No comment found"));

        if (commentDto.getAuthorId() != null && !comment.getAuthorId().equals(commentDto.getAuthorId())) {
            throw new IllegalArgumentException("You cannot change the author of the comment");
        }

        if (commentDto.getPostId() != null && !comment.getPost().getId().equals(commentDto.getPostId())) {
            throw new IllegalArgumentException("You cannot change the post of the comment");
        }

        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
