package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.kafka.CommentPostEvent;
import faang.school.postservice.dto.kafka.EventAction;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.exception.NotCommentAuthorException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.redis.CommentEventPublisher;
import faang.school.postservice.validator.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Setter
@Slf4j
public class CommentService {

    private final PostService postService;
    private final RedisCacheService redisCacheService;
    private final UserContext userContext;
    private final CommentRepository commentRepository;
    private final CommentEventPublisher redisCommentEventPublisher;
    private final KafkaCommentProducer kafkaCommentEventPublisher;
    private final CommentMapper commentMapper;
    private final RedisCommentMapper redisCommentMapper;
    private final CommentValidator commentValidator;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        long postId = commentDto.getPostId();
        long authorId = commentDto.getAuthorId();

        commentValidator.validateUserExistence(authorId);

        Post post = postService.findAlreadyPublishedAndNotDeletedPost(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with ID: %d, are not published yet or already deleted", postId)));

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        Comment entity = commentRepository.save(comment);
        log.info("Comment saved successfully.It's ID: {}", entity.getId());

        publishCommentCreationEvent(entity);
        publishKafkaCommentEvent(entity, EventAction.CREATE);

        redisCacheService.updateOrCacheUser(redisCacheService.findUserBy(authorId));

        return commentMapper.toDto(entity);
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        long commentId = Objects.requireNonNull(commentDto.getId(), "Comment ID cant be null for update");

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with ID: %d doesn't exist", commentId)));

        commentValidator.validateAuthorUpdate(comment, commentDto);
        log.info("Comment with ID: {} passed all required validation for update", commentId);

        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        publishKafkaCommentEvent(comment, EventAction.UPDATE);

        return commentMapper.toDto(comment);
    }

    public Page<CommentDto> getCommentsByPost(Long postId, Pageable pageable) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnorePaths("authorId", "verified", "post.views",
                        "post.published", "post.corrected", "post.deleted",
                        "post.verified")
                .withMatcher("post.id", ExampleMatcher.GenericPropertyMatcher::exact);

        Example<Comment> example = Example.of(buildCommentExampleBy(postId), exampleMatcher);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.asc("createdAt")));

        Page<Comment> page = commentRepository.findAll(example, pageable);

        List<CommentDto> dtos = page.get()
                .map(commentMapper::toDto)
                .toList();

        return new PageImpl<>(dtos);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Long requesterId = userContext.getUserId();
        if (requesterId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is missing");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with ID: %d doesn't exist", commentId)));

        long authorId = comment.getAuthorId();
        if (requesterId != authorId) {
            throw new NotCommentAuthorException(String.format("Comment with ID: %d can be deleted only by its author", commentId));
        }
        commentRepository.deleteById(commentId);

        publishKafkaCommentEvent(comment, EventAction.DELETE);
    }

    @Transactional
    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Comment with ID: %d doesn't exist", commentId)));
    }

    public List<Comment> findUnverifiedComments() {
        return commentRepository.findUnverifiedComments();
    }

    public void saveAll(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    private void publishCommentCreationEvent(Comment comment) {
        long commentId = comment.getId();
        long authorId = comment.getAuthorId();
        long postId = comment.getPost().getId();

        CommentEventDto commentEventDto = CommentEventDto.builder()
                .commentId(commentId)
                .authorId(authorId)
                .postId(postId)
                .createdAt(comment.getCreatedAt())
                .build();
        redisCommentEventPublisher.publish(commentEventDto);
        log.info("Comment creation event were published with comment ID: {}, author ID: {}, post ID: {}", commentId, authorId, postId);
    }

    private void publishKafkaCommentEvent(Comment comment, EventAction eventAction) {
        long postId = comment.getPost().getId();
        long authorId = comment.getAuthorId();

        CommentPostEvent event = CommentPostEvent.builder()
                .postId(postId)
                .commentDto(redisCommentMapper.toDto(comment))
                .eventAction(eventAction)
                .build();
        kafkaCommentEventPublisher.publish(event);
        log.info("Comment event with Post ID: {}, and Author ID: {}, has been successfully published", postId, authorId);
    }

    private Comment buildCommentExampleBy(long postId) {
        return Comment.builder()
                .post(Post.builder().id(postId).build())
                .build();

    }
}
