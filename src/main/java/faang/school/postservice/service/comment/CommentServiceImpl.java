package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.exception.comment.CommentException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;

    @Override
    public CommentDto addComment(CommentDto commentDto) {
        userServiceClient.getUser(commentDto.getAuthorId());

        Post post = postRepository
                .findById(commentDto.getPostId())
                .orElseThrow(()
                        -> new EntityNotFoundException(String.format("Post with ID %s not found.", commentDto.getPostId()))
                );

        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);

        publishEvent(commentDto);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void updateComment(long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with ID %s not found.", commentId)));

        long dtoAuthorId = updateCommentDto.authorId();
        if (dtoAuthorId != comment.getAuthorId()) {
            throw new CommentException(String.format("User with ID %s is not allowed to update this comment.",
                    dtoAuthorId));
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

    private void publishEvent(CommentDto commentDto) {
        CommentEvent event = new CommentEvent();
        event.setIdComment(commentDto.getId());
        event.setIdAuthor(commentDto.getAuthorId());
        event.setIdPost(commentDto.getPostId());
        event.setComment(commentDto.getContent());
        commentEventPublisher.publish(event);
    }
}
