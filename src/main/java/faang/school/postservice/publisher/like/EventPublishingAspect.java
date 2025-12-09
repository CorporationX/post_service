package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class EventPublishingAspect {

    private final LikeEventPublisher likeEventPublisher;
    private final UnlikeEventPublisher unlikeEventPublisher;
    private final PostRepository postRepository;

    @Around("@annotation(PublishLikeEvent)")
    public Object handleLikeEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long postId = (args.length > 0 && args[0] instanceof Long) ? (Long) args[0] : null;

        Long postAuthorId = null;
        if (postId != null) {
            postAuthorId = postRepository.findAuthorIdByIdOrThrow(postId);
        }

        Object result = joinPoint.proceed();

        if (!(result instanceof LikeDto likeDto)) {
            log.error("Method did not return LikeDto");
            return result;
        }

        if (postId == null || postAuthorId == null) {
            log.error("Missing required data for event publishing");
            return result;
        }

        Long userId = likeDto.userId();
        if (userId == null) {
            log.error("User ID not found in LikeDto");
            return result;
        }
        final Long finalPostAuthorId = postAuthorId;
        final Long finalUserId = userId;
        final Long finalPostId = postId;
        final Long finalLikeId = likeDto.id();
        CompletableFuture.runAsync(() -> {
            try {
                likeEventPublisher.publishLikeEvent(
                        finalPostAuthorId,
                        finalUserId,
                        finalPostId,
                        finalLikeId
                );
                log.info("Like event published asynchronously for post {} by user {}", postId, userId);
            } catch (Exception e) {
                log.error("Failed to publish like event asynchronously for post {}", postId, e);
            }
        });
        return result;
    }

    @Around("@annotation(PublishUnlikeEvent)")
    public Object handleUnlikeEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long postId = (args.length > 0 && args[0] instanceof Long) ? (Long) args[0] : null;

        Long postAuthorId = null;
        if (postId != null) {
            postAuthorId = postRepository.findAuthorIdByIdOrThrow(postId);
        }

        Object result = joinPoint.proceed();

        if (!(result instanceof LikeDto likeDto)) {
            log.error("Method did not return LikeDto");
            return result;
        }

        if (postId == null || postAuthorId == null) {
            log.error("Missing required data for event publishing");
            return result;
        }

        Long userId = likeDto.userId();
        if (userId == null) {
            log.error("User ID not found in LikeDto");
            return result;
        }

        final Long finalPostAuthorId = postAuthorId;
        final Long finalLikeAuthorId = userId;
        final Long finalPostId = postId;
        final Long finalLikeId = likeDto.id();
        CompletableFuture.runAsync(() -> {
            try {
                unlikeEventPublisher.publishUnlikeEvent(
                        finalPostAuthorId,
                        finalLikeAuthorId,
                        finalPostId,
                        finalLikeId
                );
                log.info("Unlike event published asynchronously for post {} by user {}", postId, userId);
            } catch (Exception e) {
                log.error("Failed to publish unlike event asynchronously for post {}", postId, e);
            }
        });
        return result;
    }
}