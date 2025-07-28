package faang.school.postservice.service.comments;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.kafka.events.CommentEvent;
import faang.school.postservice.kafka.events.EventType;
import faang.school.postservice.kafka.producer.DataSender;
import faang.school.postservice.kafka.producer.KafkaDataSenderImpl;
import faang.school.postservice.kafka.producer.KafkaTopics;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final KafkaDataSenderImpl kafkaDataSenderImpl;
    private final KafkaTopics kafkaTopics;

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
        log.info("Successfully created comment with id: {}", savedComment.getId());

        CommentEvent commentEvent = new CommentEvent();
        commentEvent.setPostId(post.getId());
        commentEvent.setAuthorId(comment.getAuthorId());
        commentEvent.setEventType(EventType.COMMENT_EVENT);
        kafkaDataSenderImpl.send(kafkaTopics.getCommentCreatedTopic(), commentEvent);

        return commentMapper.toDto(savedComment);
    }

    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed()) // from oldest to newest
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("No comment found"));
        if (!Objects.equals(comment.getAuthorId(), commentDto.getAuthorId())) {
            throw new IllegalArgumentException("You cannot change the author of the comment");
        }

        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public Comment getComment(Long commentId) {
        log.info("Start method getComment with commentId: {}", commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment not found!"));
    }
}