package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.ModerationProperties;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.moderation.ModerationDictionaryComment;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostValidator postValidator;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final ImageService imageService;
    private final CommentEventPublisher commentEventPublisher;
    private final ModerationDictionaryComment moderationDictionaryComment;
    private final ModerationProperties moderationProperties;
    private final TaskExecutor asyncModerationExecutor;
    private final KafkaCommentProducer commentProducer;

    public CommentDto createComment(Long postId, CommentDto commentDto) {
        commentValidator.validateCommentDto(commentDto);

        postValidator.getPostById(postId);

        validateUserId(commentDto.getAuthorId());

        Comment comment = commentMapper.toComment(commentDto);
        comment = commentRepository.save(comment);
        comment = saveCommentWithImage(commentDto, comment);
        CommentDto resultDto = commentMapper.toCommentDto(comment);

        publishCommentEvent(resultDto);

        commentProducer.sendCommentEvent(
                CommentEvent.builder()
                        .postId(postId)
                        .commentId(comment.getId())
                        .text(comment.getContent())
                        .authorId(comment.getAuthorId())
                        .createdAt(comment.getCreatedAt())
                        .build()
        );

        return resultDto;
    }

    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        commentValidator.validateCommentDto(commentDto);
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with ID " + commentId + " not found"));

        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentMapper.toCommentDto((commentRepository.save(comment)));
    }

    public List<CommentDto> getAllComments(Long postId) {
        commentValidator.validateListComments(postId);

        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID" + commentId));

        Stream.of(
                        comment.getLargeImageFileKey(),
                        comment.getSmallImageFileKey()
                )
                .forEach(imageService::deleteImageIfExists);

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void moderateUnverifiedComments() {
        List<Comment> comments = commentRepository.findByVerifiedIsNull();

        if (comments.isEmpty()) {
            log.info("No comments to moderate.");
            return;
        }

        log.info("Found {} unverified comments to process", comments.size());

        List<List<Comment>> chunks = new ArrayList<>();
        for (int i = 0; i < comments.size(); i += moderationProperties.getChunkSize()) {
            chunks.add(comments.subList(i, Math.min(i + moderationProperties.getChunkSize(), comments.size())));
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        int chunkIndex = 0;
        for (List<Comment> chunk : chunks) {
            int currentChunkIndex = chunkIndex++;
            futures.add(
                    CompletableFuture.runAsync(() -> {
                        try {
                            log.info("Starting moderation for chunk #{}", currentChunkIndex);
                            processChunk(chunk);
                            log.info("Finished moderation for chunk #{}", currentChunkIndex);
                        } catch (Exception e) {
                            log.error("Error while moderating chunk #{}: {}", currentChunkIndex, e.getMessage(), e);
                        }
                    }, runnable -> asyncModerationExecutor.execute(runnable))
            );
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("Moderation finished.");
    }

    private void validateUserId(long userId) {
        userServiceClient.getUser(userId);
    }

    @Transactional
    protected void processChunk(List<Comment> chunk) {
        for (Comment comment : chunk) {
            boolean hasBadWords = moderationDictionaryComment.containsBadWords(comment.getContent());
            comment.setVerified(!hasBadWords);
            comment.setVerifiedDate(LocalDateTime.now());
        }
        commentRepository.saveAll(chunk);
    }

    private Comment saveCommentWithImage(CommentDto commentDto, Comment comment) {
        MultipartFile image = commentDto.getImage();
        if (image != null && !image.isEmpty()) {
            ImageService.ImageKeys keys = imageService.uploadResizedImages(image, comment.getId());

            comment.setLargeImageFileKey(keys.largeKey());
            comment.setSmallImageFileKey(keys.smallKey());

            comment = commentRepository.save(comment);
        }
        return comment;
    }

    private void publishCommentEvent(CommentDto dto) {
        CommentEvent event = new CommentEvent(
                dto.getAuthorId(),
                dto.getPostId(),
                dto.getId(),
                dto.getContent(),
                LocalDateTime.now()
        );

        commentEventPublisher.publish(event);
    }
}
