package faang.school.postservice.dto.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventAspect {
    private final ApplicationEventPublisher eventPublisher;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    @Around("@annotation(PublishPostEvent)")
    public Object publishPostEvent(ProceedingJoinPoint joinPoint) throws  Throwable {
        Long postId = extractPostId(joinPoint);

        Object result = joinPoint.proceed();

        if (result != null) {
            publishEvent(postId, result);
        }

        return result;
    }

    private Long extractPostId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < parameterNames.length; i++) {
            if (("postId".equals(parameterNames[i]) || "id".equals(parameterNames[i]))
                    && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }

        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }

        throw new IllegalArgumentException("Post ID not found in method arguments");
    }

    private void publishEvent(Long postId, Object methodResult) {
        try {
            long userId = userContext.getUserId();
            UserDto user = userServiceClient.getUser(userId);

            Long authorId = extractAuthorId(methodResult);

            PostViewEvent event = new PostViewEvent(
                    postId,
                    authorId,
                    userId,
                    LocalDateTime.now()
            );
            eventPublisher.publishEvent(event);

        } catch (Exception e) {
            log.error("Failed to publish PostViewEvent: {}", e.getMessage());
        }
    }

    private Long extractAuthorId(Object methodResult) {
        if (methodResult instanceof PostV2Dto) {
            return ((PostV2Dto) methodResult).authorId();
        }
        throw new IllegalStateException("Cannot extract authorId from method result");
    }
}
