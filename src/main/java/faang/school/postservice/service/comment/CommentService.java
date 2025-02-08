package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateRequestDto;
import faang.school.postservice.dto.events_dto.CommentEventDto;
import faang.school.postservice.dto.user.UserForBanEventDto;
import faang.school.postservice.event_sender.CommentEventSender;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.UserBanEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.ModerationDictionary;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;
    private final UserBanEventPublisher banPublisher;
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;
    private final CommentEventSender commentEventSender;

    public CommentResponseDto createComment(CommentRequestDto commentRequestDto) {
        commentValidator.validateAuthorExists(commentRequestDto.getAuthorId());
        commentValidator.validatePostExists(commentRequestDto.getPostId());

        Comment comment = commentMapper.toEntity(commentRequestDto);
        comment.setLikes(new ArrayList<>());

        if (!moderationDictionary.isVerified(comment.getContent())) {
            comment.setVerified(false);
        }

        Comment savedComment = commentRepository.save(comment);
        CommentResponseDto commentResponseDto = commentMapper.toDto(savedComment);
        log.info("New comment with id: {} created", comment.getId());

        CommentEventDto commentEventDto = createCommentEventDto(commentResponseDto);
        commentEventPublisher.publish(commentEventDto);
        log.info("Notification about new comment sent to notification service {}", commentEventDto);

        commentEventSender.sendEvent(comment);

        return commentResponseDto;
    }

    private CommentEventDto createCommentEventDto(CommentResponseDto commentResponseDto) {
        CommentEventDto commentEventDto = new CommentEventDto();
        commentEventDto.setPostAuthorId(postRepository.getPostById(commentResponseDto.getPostId()).getAuthorId());
        commentEventDto.setCommentAuthorId(commentResponseDto.getAuthorId());
        commentEventDto.setPostId(commentResponseDto.getPostId());
        commentEventDto.setCommentId(commentResponseDto.getId());
        commentEventDto.setCommentContent(commentResponseDto.getContent());
        commentEventDto.setCommentedAt(LocalDateTime.now());
        return commentEventDto;
    }

    public CommentResponseDto updateComment(CommentUpdateRequestDto commentUpdateRequestDto) {
        Comment commentToUpdate = commentRepository.getCommentById(commentUpdateRequestDto.getCommentId());

        String postContent = commentUpdateRequestDto.getContent();
        commentToUpdate.setContent(postContent);

        commentRepository.save(commentToUpdate);
        log.info("Comment with id: {} updated", commentUpdateRequestDto.getCommentId());
        return commentMapper.toDto(commentToUpdate);
    }

    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        commentValidator.validatePostExists(postId);

        List<Comment> commentsByPostId = commentRepository.findAllByPostId(postId);
        commentsByPostId.sort(Comparator.comparing(Comment::getCreatedAt).reversed());

        log.info("Retrieved all the comments for the post with id: {}", postId);
        return commentMapper.toDto(commentsByPostId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        log.info("Comment with id: {} deleted", commentId);
    }

    @Async("moderationPool")
    @Transactional
    public void commenterBanner() {
        commentRepository.getAuthorIdsForBanFromComments().forEach(authorId -> {
            UserForBanEventDto eventDto = new UserForBanEventDto();
            eventDto.setId(authorId);
            banPublisher.publish(eventDto);
            List<Comment> commentsFromUser = commentRepository.findAllByAuthorId(authorId);
            commentsFromUser.forEach(comment -> comment.setVision(false));
            log.info("Author with authorId {} is banned", authorId);
        });
    }

}