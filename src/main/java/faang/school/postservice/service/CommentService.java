package faang.school.postservice.service;

import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.events.CommentEventPublisher;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.event.CommentEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentEventPublisher commentEventPublisher;
    private final CommentEventMapper commentEventMapper;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository,
            CommentEventPublisher commentEventPublisher,
            CommentEventMapper commentEventMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentEventPublisher = commentEventPublisher;
        this.commentEventMapper = commentEventMapper;
    }

    @Transactional
    public Comment createComment(Long postId, Long authorId, String content) {
        validateInput(postId, authorId, content);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        Comment comment = Comment.builder()
                .post(post)
                .authorId(authorId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        comment = commentRepository.save(comment);
        publishEventIfNeeded(comment, post);
        return comment;
    }

    private void publishEventIfNeeded(Comment comment, Post post) {
        if (!Objects.equals(comment.getAuthorId(), post.getAuthorId())) {
            try {
                CommentEvent event = commentEventMapper.toEvent(comment, post);
                commentEventPublisher.publishCommentEvent(event);
            } catch (Exception e) {
                log.warn("Failed to publish comment event for comment {}: {}",
                        comment.getId(), e.getMessage());
            }
        }
    }

    private void validateInput(Long postId, Long authorId, String content) {
        if (postId == null || postId <= 0) {
            throw new InvalidParameterException("Invalid post ID");
        }
        if (authorId == null || authorId <= 0) {
            throw new InvalidParameterException("Invalid author ID");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidParameterException("Comment content cannot be empty");
        }
        if (content.length() > 1000) {
            throw new InvalidParameterException("Comment too long");
        }
    }
}