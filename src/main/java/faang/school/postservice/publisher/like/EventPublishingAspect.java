package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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

    @AfterReturning(
            pointcut = "@annotation(PublishLikeEvent)",
            returning = "likeDto")
    public void publishLikeEvent(JoinPoint joinPoint, LikeDto likeDto) {
        publishEvent(joinPoint, likeDto, true);
    }

    @AfterReturning(
            pointcut = "@annotation(PublishUnlikeEvent)",
            returning = "likeDto")
    public void publishUnlikeEvent(JoinPoint joinPoint, LikeDto likeDto) {
        publishEvent(joinPoint, likeDto, false);
    }


    private void publishEvent(JoinPoint joinPoint, LikeDto likeDto, boolean isLike) {
        if (likeDto == null || likeDto.userId() == null) {
            log.error("LikeDto or userId is null, event will not publisher");
            return;
        }

        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || !(args[0] instanceof Long postId)) {
            log.error("PostId not found in method arguments");
        }

        CompletableFuture.runAsync(() -> {
            try {
                if (isLike) {
                    likeEventPublisher.publishLikeEvent(
                            likeDto.postAuthorId(),
                            likeDto.userId(),
                            likeDto.postId(),
                            likeDto.id()
                    );
                    log.info("Like event published for post {} by user {}", likeDto.postId(), likeDto.userId());
                } else {
                    unlikeEventPublisher.publishUnlikeEvent(
                            likeDto.postAuthorId(),
                            likeDto.userId(),
                            likeDto.postId(),
                            likeDto.id()
                    );
                    log.info("Like event published for post {} by user {}", likeDto.postId(), likeDto.userId());
                }
            } catch (Exception e) {
                log.error("Failed publish {} event for post {}", isLike ? "like" : "unlike", likeDto.postId(), e);
            }
        });
    }
}
