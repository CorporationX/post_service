package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.cache.author.EventAuthorDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.kafka.comment.CommentCreatedEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.comment.KafkaCommentProducer;
import faang.school.postservice.publisher.comment.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.author.AuthorCacheRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentEventPublisher commentEventPublisher;
    private final AuthorCacheRepository authorCacheRepository;
    private final KafkaCommentProducer kafkaCommentProducer;

    public List<Comment> getUnverifiedComments() {
        return commentRepository.findByVerifiedAtIsNull();
    }

    public void saveComments(List<Comment> comments) {
        log.info("Trying to save comments in db");
        commentRepository.saveAll(comments);
        log.info("Comments saved");
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public CommentDto createComment(long postId, CreateCommentRequest createCommentRequest) {
        UserDto userDto = userServiceClient.getUser(createCommentRequest.getAuthorId());
        log.info("[{}] Validation successful for postId: {}, createCommentRequest: {}", "createComment",
                postId, createCommentRequest);
        authorCacheRepository.save(EventAuthorDto.builder()
                .authorId(userDto.getId())
                .followerIds(userDto.getFollowers())
                .build());

        Comment comment = commentMapper.toComment(createCommentRequest);
        log.info(" [{}] Mapping of CommentDto to Comment entity successful for postId: {}, "
                + "createCommentRequest'{}', comment: {}", "createComment", postId, createCommentRequest, comment);

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post with id " + postId + " not found"));
        comment.setPost(post);
        log.info("[{}] Post with id: {} successfully linked to Comment", "createComment", postId);

        comment = commentRepository.save(comment);
        log.info("[{}] Comment successfully saved to DB with ID: {}", "createComment", comment.getId());

        commentEventPublisher.publish(commentMapper.toCommentEventDto(comment));
        sendCommentEvent(comment, userDto);
        return commentMapper.toCommentDto(comment);
    }

    public CommentDto updateComment(long postId, long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment oldComment = commentRepository.findById(commentId).orElseThrow(() ->
                new DataValidationException("Comment is null"));
        commentValidator.checkingForCompliance(oldComment, updateCommentRequest);
        log.info("[{}] Validation successful for postId: {}, updateCommentRequest: {}", "updateComment",
                postId, updateCommentRequest);

        Comment comment = commentMapper.toComment(updateCommentRequest);
        log.info(" [{}] Mapping of CommentDto to Comment entity successful for postId: {}, "
                + "updateCommentRequest: '{}', comment: {}", "updateComment", postId, updateCommentRequest, comment);

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post with id " + postId + " not found"));
        comment.setPost(post);
        log.info("[{}] Post with id: {} successfully linked to Comment", "updateComment", postId);

        comment = commentRepository.save(comment);
        log.info("[{}] Comment successfully updated to DB with ID: {}", "createComment", comment.getId());

        return commentMapper.toCommentDto(comment);
    }

    public List<CommentDto> getAllComments(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        if (comments.isEmpty()) {
            throw new NoSuchElementException("List of comments is Empty");
        }

        comments.sort(Comparator.comparing(Comment::getCreatedAt).reversed());
        log.info("[{}] we have successfully read the list of comments:/n {}  "
                + "from the database and sorted the list.", "getAllComments", comments);

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
        log.info("[{}] the comment with id: {} was successfully deleted", "deleteComment", commentId);
    }

    private void sendCommentEvent(Comment comment, UserDto author) {
        CommentCreatedEvent commentCreatedEvent = CommentCreatedEvent.builder()
                .commentId(comment.getId())
                .authorId(author.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .postId(comment.getPost().getId())
                .build();
        try {
            kafkaCommentProducer.sendEvent(commentCreatedEvent);
        } catch (Exception ex) {
            log.error("Failed to send postCreateEvent: {}", commentCreatedEvent.toString(), ex);
        }
    }
}
