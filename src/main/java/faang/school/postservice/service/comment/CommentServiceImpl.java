package faang.school.postservice.service.comment;

import faang.school.postservice.client.CommentAnalyzer;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.contants.ErrorMessage;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exception.CommentAnalyzerException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.InvalidCommentContentException;
import faang.school.postservice.exception.NotAuthorException;
import faang.school.postservice.exception.NullEntityException;
import faang.school.postservice.mapper.CommentRequestMapper;
import faang.school.postservice.mapper.CommentResponseMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static faang.school.postservice.contants.ErrorMessage.ERROR_NOT_AUTHOR_COMMENT;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_AUTHOR_ID;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_COMMENT_ID;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_CONTENT;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_POST_ID;
import static faang.school.postservice.contants.InfoMessage.INFO_CREATE_COMMENT;
import static faang.school.postservice.contants.InfoMessage.INFO_DELETE_COMMENT;
import static faang.school.postservice.contants.InfoMessage.INFO_GET_COMMENTS;
import static faang.school.postservice.contants.InfoMessage.INFO_UPDATE_COMMENT;


@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    @Value("${moderation.comments.batch-size}")
    private int commentModerationBatchSize;

    @Value("${moderation.comments.max-attempts}")
    private int commentModerationMaxAttempts;

    @Value("${moderation.comments.backoff-delay}")
    private int commentModerationBackoffDelay;

    @Value("${moderation.comments.timeout-hours}")
    private int commentModerationTimeoutHours;

    private static final double TOXICITY_THRESHOLD = 0.35;
    private static final int MAX_LENGTH_CHARACTER = 4096;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentAnalyzer commentAnalyzer;
    private final CommentRequestMapper commentRequestMapper;
    private final CommentResponseMapper commentResponseMapper;
    private final UserServiceClient userServiceClient;

    public Mono<Void> moderateComments() {
        log.info("Comment moderation started");
        return Mono.fromCallable(commentRepository::count)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(count -> {
                    int batches = (int) (count + commentModerationBatchSize - 1) / commentModerationBatchSize;
                    return Flux.range(0, batches);
                })
                .flatMap(batchNumber -> {
                    Pageable pageable = PageRequest.of(batchNumber, commentModerationBatchSize);

                    return Mono.fromCallable(() -> commentRepository.findComments(pageable))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMapMany(page -> Flux.fromIterable(page.getContent()));
                })
                .flatMap(this::moderateComment)
                .timeout(Duration.ofHours(commentModerationTimeoutHours))
                .then()
                .doOnSuccess(v -> log.info("Comment moderation completed"))
                .doOnError(e -> log.error("Error while moderating comments", e));
    }

    public void createComment(CommentRequestDto commentRequestDto) {
        validateCreateComment(commentRequestDto);
        Post post = getPost(commentRequestDto.getPostId());
        Comment comment = commentRequestMapper.toComment(commentRequestDto);
        comment.setPost(post);
        comment.setAuthorId(commentRequestDto.getAuthorId());
        commentRepository.save(comment);
        log.info(INFO_CREATE_COMMENT, comment.getId(), commentRequestDto.getAuthorId(), commentRequestDto.getPostId());
    }

    public void updateComment(Long id, CommentUpdateDto commentUpdateDto) {
        validateContent(commentUpdateDto.getContent());
        validateId(commentUpdateDto.getAuthorId(), ERROR_NULL_AUTHOR_ID);
        Comment comment = getComment(id);
        isAuthorComment(comment, commentUpdateDto);
        comment.setContent(commentUpdateDto.getContent());
        commentRepository.save(comment);
        log.info(INFO_UPDATE_COMMENT, id, commentUpdateDto.getAuthorId());
    }

    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        getPost(postId);
        List<CommentResponseDto> commentResponseDto = commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentResponseMapper::toCommentDto)
                .toList();
        log.info(INFO_GET_COMMENTS, commentResponseDto.size(), postId);
        return commentResponseDto;
    }

    public void deleteComment(Long id) {
        getComment(id);
        commentRepository.deleteById(id);
        log.info(INFO_DELETE_COMMENT, id);
    }

    private Comment getComment(Long id) {
        validateId(id, ERROR_NULL_COMMENT_ID);
        return getEntity(() -> commentRepository.findById(id), ErrorMessage.getErrorNotFoundComment(id));
    }

    private Post getPost(Long id) {
        validateId(id, ERROR_NULL_POST_ID);
        return getEntity(() -> postRepository.findById(id), ErrorMessage.getErrorNotFoundPost(id));
    }

    private void validateCreateComment(CommentRequestDto commentDto) {
        validateContent(commentDto.getContent());
        validateId(commentDto.getAuthorId(), ERROR_NULL_AUTHOR_ID);
        validateId(commentDto.getPostId(), ERROR_NULL_POST_ID);
        validateUserFromClient(commentDto.getAuthorId());
    }

    private void validateContent(String content) {
        if (content == null) {
            log.error(ERROR_NULL_CONTENT);
            throw new NullEntityException(ERROR_NULL_CONTENT);
        }
        if (content.isBlank() || content.length() > MAX_LENGTH_CHARACTER) {
            String errorMessage = ErrorMessage.getErrorWrongFormatContent(MAX_LENGTH_CHARACTER);
            log.error(errorMessage);
            throw new InvalidCommentContentException(errorMessage);
        }
    }

    private void validateId(Long id, String errorMessage) {
        if (id == null) {
            log.error(errorMessage);
            throw new NullEntityException(errorMessage);
        }
    }

    private void validateUserFromClient(Long id) {
        try {
            userServiceClient.getUser(id);
        } catch (FeignException.NotFound e) {
            log.error(ErrorMessage.getErrorNotFoundUser(id), e);
            throw new IllegalArgumentException(ErrorMessage.getErrorNotFoundUser(id), e);
        } catch (FeignException e) {
            log.error(ErrorMessage.getErrorOccurredValidatingUser(id), e);
            throw new RuntimeException(ErrorMessage.getErrorOccurredValidatingUser(id), e);
        }
    }

    private void isAuthorComment(Comment comment, CommentUpdateDto dto) {
        if (!comment.getAuthorId().equals(dto.getAuthorId())) {
            log.error(ERROR_NOT_AUTHOR_COMMENT);
            throw new NotAuthorException(ERROR_NOT_AUTHOR_COMMENT);
        }
    }

    private <T> T getEntity(Supplier<Optional<T>> finder, String errorMessage) {
        return finder.get().orElseThrow(() -> {
            log.error(errorMessage);
            return new EntityNotFoundException(errorMessage);
        });
    }

    private Mono<Void> moderateComment(Comment comment) {
        return commentAnalyzer.analyzeComment(comment.getContent())
                .retryWhen(Retry.backoff(commentModerationMaxAttempts, Duration.ofSeconds(commentModerationBackoffDelay))
                        .filter(ex -> ex instanceof CommentAnalyzerException))
                .flatMap(toxicityScore -> {
                    boolean moderationFailed = toxicityScore.getAttributeScores().values().stream()
                            .anyMatch(attributeScore -> attributeScore.getSummaryScore().getValue()
                                    >= TOXICITY_THRESHOLD || attributeScore.getSpanScores().stream().anyMatch(
                                    spanScore -> spanScore.getScore().getValue() >= TOXICITY_THRESHOLD));

                    log.debug("Comment with ID {} and content '{}' {} moderation",
                            comment.getId(), comment.getContent(), moderationFailed ? "failed" : "passed");
                    comment.setVerified(!moderationFailed);
                    comment.setVerifiedDate(LocalDateTime.now());

                    return Mono.fromCallable(() -> commentRepository.save(comment))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then();
                })
                .onErrorResume(e -> {
                    log.error("Could not moderate comment with ID {} and content {}",
                            comment.getId(), comment.getContent());
                    return Mono.empty();
                });
    }
}
