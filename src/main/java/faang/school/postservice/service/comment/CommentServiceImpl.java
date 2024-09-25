package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Override
    @SneakyThrows
    public CommentDto addComment(CommentDto commentDto) {
        try {
            userServiceClient.getUser(commentDto.getAuthorId());
        } catch (Exception exception) {
            throw new Exception(String.format("An error occurred in the process of obtaining a user with ID %s",
                    commentDto.getAuthorId()));
        }

        Post post = postRepository
                .findById(commentDto.getPostId())
                .orElseThrow(()
                        -> new EntityNotFoundException(String.format("Post with ID %s not found.", commentDto.getPostId()))
                );

        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    @SneakyThrows
    public void updateComment(long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with ID %s not found.", commentId)));

        long authorId = comment.getAuthorId();
        if (updateCommentDto.authorId() != authorId) {
            throw new AccessDeniedException(String.format("User with ID %s is not allowed to update this comment.",
                    updateCommentDto.authorId()));
        }

        commentRepository.updateContentAndDateById(commentId, updateCommentDto.content(), LocalDateTime.now());
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.getByPostIdOrderByCreatedAtDesc(postId);
        return commentMapper.toDto(comments);
    }

    @Override
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }
}
