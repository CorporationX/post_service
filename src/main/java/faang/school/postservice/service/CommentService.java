package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentCreateEventDto;
import faang.school.postservice.dto.comment.CommentResponse;
import faang.school.postservice.dto.comment.CommentUpdateRequest;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.exceptions.FileIsEmptyException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import faang.school.postservice.utils.ImageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static faang.school.postservice.config.MinioBuckets.COMMENT_IMAGE_BUCKET_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private static final int SMALL_IMAGE_SIZE = 170;
    private static final int LARGE_IMAGE_SIZE = 1080;
    private final ValidateService validateService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final ImageService imageService;
    private final KafkaService kafkaService;
    private final PostService postService;
    private final ModerationDictionary moderationDictionary;

    @Transactional
    public CommentResponse create(@Valid CreateCommentRequest createCommentRequest) {
        validateService.validateUser(createCommentRequest.userId());
        validateService.validatePost(createCommentRequest.postId());

        Post post = postService.getPostById(createCommentRequest.postId());
        Comment comment = commentMapper.toEntity(createCommentRequest);
        comment = commentRepository.save(comment);
        CommentCreateEventDto eventDto = CommentCreateEventDto.builder()
                .authorId(comment.getAuthorId())
                .postId(post.getId())
                .content(comment.getContent())
                .build();
        kafkaService.sendCommentCreateMessage(eventDto);
        return commentMapper.toCommentResponse(comment);
    }

    @Transactional
    public CommentResponse update(@Valid CommentUpdateRequest updateRequest) {
        Comment comment = getComment(updateRequest.id());
        comment.setContent(updateRequest.content());
        return commentMapper.toCommentResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getAllByPostId(@Valid @NotNull @Positive Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    @Transactional
    public void delete(@Valid @NotNull @Positive Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment with id " + commentId + " not found");
        }
        commentRepository.deleteById(commentId);
    }

    public boolean existsById(long commentId) {
        return commentRepository.existsById(commentId);
    }

    @Transactional
    public void uploadImage(@Valid @Positive Long commentId, MultipartFile file) {
        Comment comment = getComment(commentId);

        if (file.isEmpty()) {
            throw new FileIsEmptyException("");
        }

        String smallImageFileId = imageService.saveImage(file, SMALL_IMAGE_SIZE, COMMENT_IMAGE_BUCKET_NAME);
        String largeImageFileId = imageService.saveImage(file, LARGE_IMAGE_SIZE, COMMENT_IMAGE_BUCKET_NAME);

        comment.setLargeImageFileKey(largeImageFileId);
        comment.setSmallImageFileKey(smallImageFileId);
    }

    private Comment getComment(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("commentId cannot be null");
        }

        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id " + commentId + " not found"));
    }

    @Async("moderateTaskExecutor")
    @Transactional
    public CompletableFuture<Void> moderateComments(List<Comment> comments) {
        return CompletableFuture.runAsync(() -> {
            for (Comment comment : comments) {
                boolean hasBadWords = moderationDictionary.containsBadWord(comment.getContent());
                comment.setVerified(!hasBadWords);
                comment.setVerifiedAt(LocalDateTime.now());
            }
            commentRepository.saveAll(comments);
            log.info("{} - {} comment checked", Thread.currentThread().getName(), comments.size());
        });
    }
}
