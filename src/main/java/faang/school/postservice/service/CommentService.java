package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.dto.event.CommentAddEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.Author;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisAuthorRepository;
import faang.school.postservice.publisher.kafka.KafkaEventPublisher;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final RedisAuthorRepository redisAuthorRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;
    private final KafkaEventPublisher kafkaEventPublisher;
    @Value("${spring.kafka.topics.comments}")
    private String commentsTopic;
    @Value("${redis.author.ttl}")
    private long authorTtl;

    public CommentDto addNewComment(long postId, CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto.getId());
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = getPostById(postId);
        comment.setPost(post);
        comment.setLikes(new ArrayList<>());
        Comment savedComment = commentRepository.save(comment);

        redisAuthorRepository.save(Author.builder()
                .expiration(authorTtl)
                .id(comment.getAuthorId())
                .commentId(comment.getId())
                .build());

        commentEventPublisher.publish(CommentEventDto.builder()
                .authorId(comment.getAuthorId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorPostId(post.getAuthorId())
                .postId(post.getId())
                .build());

        CommentAddEvent commentAddEvent = new CommentAddEvent(
                savedComment.getAuthorId(),
                savedComment.getPost().getId(),
                savedComment.getPost().getAuthorId(),
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getCreatedAt());

        kafkaEventPublisher.sendEvent(commentsTopic, commentAddEvent);

        return commentMapper.toDTO(savedComment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto.getId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setContent(commentDto.getContent());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<CommentDto> getAllComments(long postId) {
        List<Comment> allByPostId = commentRepository.findAllByPostId(postId);
        return commentMapper.toDtoList(allByPostId);
    }

    public Comment getComment(long id) {
        return commentRepository.findById(id).orElseThrow();
    }

    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There are no posts with that id: " + postId));
    }
}