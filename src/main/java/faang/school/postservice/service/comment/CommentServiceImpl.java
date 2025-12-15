package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;
    private final UserContext userContext;

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Post with id %d not found", commentDto.getPostId())));
        Long currentUserId = userContext.getUserId();

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(currentUserId);
        comment.setPost(post);

        Comment savedComment  = commentRepository.save(comment);
        log.info("Comment #{} created for post #{} by user #{}",
                savedComment.getId(), post.getId(), currentUserId);

        if (!currentUserId.equals(post.getAuthorId())) {
            publishCommentEvent(savedComment, post);
        }

        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = getCommentOrThrow(commentId);
        validateCommentAuthor(comment);
        comment.setContent(commentDto.getContent());

        log.info("Comment #{} updated", commentId);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        validateCommentAuthor(comment);

        commentRepository.delete(comment);
        log.info("Comment #{} deleted", commentId);
    }


    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsByPostId(Long postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(
                    String.format("Post with id %d not found", postId)
            );
        }
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(commentMapper::toDto);
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Comment with id %d not found", commentId)));
    }

    private void validateCommentAuthor(Comment comment) {
        Long currentUserId = userContext.getUserId();
        if (!comment.getAuthorId().equals(currentUserId)) {
            throw new IllegalArgumentException("You can only modify your own comments");
        }
    }

    private void publishCommentEvent(Comment comment, Post post) {
        CommentEvent event = CommentEvent.builder()
                .commentId(comment.getId())
                .commentAuthorId(comment.getAuthorId())
                .postAuthorId(post.getAuthorId())
                .postId(post.getId())
                .commentText(comment.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        commentEventPublisher.publish(event);
        log.info("Comment event published for comment #{}", comment.getId());
    }
}
