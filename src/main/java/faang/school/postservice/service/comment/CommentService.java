package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.EventType;
import faang.school.postservice.publisher.KafkaEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final KafkaEventPublisher kafkaPublisher;

    @Transactional
    public CommentDto createComment(CommentCreateDto commentCreateDto) {
        log.info("Creating a comment for post ID: {} by user ID: {}", commentCreateDto.getPostId(), commentCreateDto.getAuthorId());

        if (!postService.existsById(commentCreateDto.getPostId())) {
            throw new EntityNotFoundException("Post with ID " + commentCreateDto.getPostId() + " does not exist.");
        }

        try {
            UserDto userDto = userServiceClient.getUser(commentCreateDto.getAuthorId());
            if (userDto == null) {
                throw new EntityNotFoundException("User not found with ID: " + commentCreateDto.getAuthorId());
            }

            Post post = postService.getPostById(commentCreateDto.getPostId());

            Comment comment = commentMapper.toEntity(commentCreateDto);
            comment.setPost(post);

            Comment savedComment = commentRepository.save(comment);
            log.info("Comment created with ID: {}", savedComment.getId());

            EventType eventType = EventType.COMMENT_CREATED;

            CommentEventDto commentEvent = CommentEventDto.builder()
                    .commentId(savedComment.getId())
                    .commenterId(commentCreateDto.getAuthorId())
                    .postId(commentCreateDto.getPostId())
                    .postAuthorId(post.getAuthorId())
                    .text(commentCreateDto.getContent())
                    .build();

            kafkaPublisher.publish(eventType, commentEvent);

            return commentMapper.toDto(savedComment);

        } catch (FeignException e) {
            log.error("Error while fetching user from userService: {}", e.getMessage());
            throw new EntityNotFoundException("Error while verifying user existence: " + e.getMessage());
        }
    }

    @Transactional
    public CommentDto updateComment(long commentId, CommentUpdateDto commentUpdateDto) {
        log.info("Updating comment with ID: {}", commentId);

        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        if (!existingComment.getAuthorId().equals(commentUpdateDto.getAuthorId())) {
            throw new EntityNotFoundException("Only the author of the comment can update it.");
        }

        commentMapper.updateEntity(commentUpdateDto, existingComment);

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment with ID: {} updated successfully.", commentId);

        return commentMapper.toDto(updatedComment);
    }

    public List<CommentDto> getAllCommentsByPostId(long postId) {
        log.info("Fetching comments for post ID: {} in chronological order", postId);

        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .map(commentMapper::toDto)
                .sorted(Comparator.comparing(CommentDto::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public void deleteComment(long commentId, long authorId) {
        log.info("Deleting comment with ID: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment not found with ID: " + commentId));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new DataValidationException("Only the author of the comment can delete it.");
        }

        commentRepository.deleteById(commentId);
        log.info("Comment with ID: {} has been deleted.", commentId);
    }
}