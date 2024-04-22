package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.kafka.CommentKafkaPublisher;
import faang.school.postservice.publisher.redis.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.RedisCommentCacheService;
import faang.school.postservice.service.redis.RedisUserCacheService;
import faang.school.postservice.validator.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;
    private final RedisCommentCacheService redisCommentCacheService;
    private final RedisUserCacheService redisUserCacheService;
    private final CommentKafkaPublisher commentKafkaPublisher;

    public CommentDto addNewComment(long postId, CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto.getId());
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = getPostById(postId);
        comment.setPost(post);
        comment.setLikes(new ArrayList<>());
        Comment savedComment = commentRepository.save(comment);
        commentEventPublisher.publish(CommentEventDto.builder()
                .authorId(comment.getAuthorId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorPostId(post.getAuthorId())
                .postId(post.getId())
                .build());

        redisUserCacheService.saveUser(post.getAuthorId());
        commentKafkaPublisher.publish(savedComment.getId(), postId, KafkaKey.SAVE);

        return commentMapper.toDTO(savedComment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto.getId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setContent(commentDto.getContent());
        Comment savedComment = commentRepository.save(comment);

        commentKafkaPublisher.publish(savedComment.getId(), savedComment.getPost().getId(), KafkaKey.SAVE);
        return commentMapper.toDTO(savedComment);
    }

    public void deleteComment(long commentId) {
        commentRepository.findById(commentId)
                .ifPresent(comment -> {
                    commentRepository.deleteById(commentId);
                    commentKafkaPublisher.publish(commentId, comment.getPost().getId(), KafkaKey.DELETE);
                });
    }

    public List<CommentDto> getAllComments(long postId) {
        List<Comment> allByPostId = commentRepository.findAllByPostId(postId);
        return commentMapper.toDtoList(allByPostId);
    }

    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There are no posts with that id: " + postId));
    }

    public Comment getComment (long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found in database"));
    }
}