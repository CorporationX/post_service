package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.ModerationDictionary;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentValidator commentValidator;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CommentEventPublisher commentEventPublisher;
    private final ModerationDictionary moderationDictionary;

    @Value("${moderation.batch-size}")
    private int moderationBachSize;

    @Value("${moderation.days-period}")
    private int moderationDaysPeriod;

    public Comment findEntityById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Comment with id '%s' not found", id)));
    }

    public CommentDto createComment(CommentDto commentDto) {
        commentValidator.validateCreation(commentDto);
        Post post = postService.findEntityById(commentDto.getPostId());

        commentDto.setCreatedAt(LocalDateTime.now());
        commentDto.setUpdatedAt(LocalDateTime.now());

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        comment = commentRepository.save(comment);

        publishCommentCreationEvent(comment);

        return commentMapper.toDto(comment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        if (commentDto.getId() == null) {
            throw new DataValidationException("Comment id is required for update");
        }
        Comment comment = findEntityById(commentDto.getId());
        commentValidator.validateUpdate(comment, commentDto);
        commentDto.setUpdatedAt(LocalDateTime.now());

        commentMapper.update(comment, commentDto);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getComments(long postId) {
        return commentRepository.findAllByPostId(postId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto)
                .toList();
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public void checkProfanities() {
        List<Comment> commentsForChecking = new ArrayList<>(commentRepository
                .findNotCheckedToVerificationComments(LocalDateTime.now().minusDays(moderationDaysPeriod)));
        List<CompletableFuture<Void>> futures = ListUtils
                .partition(commentsForChecking, moderationBachSize)
                .stream()
                .map(comments -> CompletableFuture.runAsync(() -> checkCommentsBatchForProfanities(comments)))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void checkCommentsBatchForProfanities(List<Comment> comments) {
        comments.forEach(comment -> {
            log.info("Checking content {} for profanities", comment.getContent());
            comment.setVerified(!moderationDictionary.containsProfanity(comment.getContent()));
            comment.setVerifiedDate(LocalDateTime.now());
            commentRepository.save(comment);
        });
    }

    private void publishCommentCreationEvent(Comment comment) {
        commentEventPublisher.publish(new CommentEvent(
                comment.getAuthorId(),
                comment.getPost().getId(),
                comment.getId(),
                comment.getContent()
        ));
    }
}
