package faang.school.postservice.aop;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.kafka.producer.post.KafkaProducerPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishPostEventAspect {

    private final UserServiceClient userServiceClient;
    private final KafkaProducerPostService kafkaProducerPostService;

    @AfterReturning(
            pointcut = "execution(* faang.school.postservice.service.post.PostV2Service.publishPost(..))",
            returning = "result"
    )
    public void publishPostEvent(JoinPoint joinPoint, Object result) {
        if (!(result instanceof PostV2Dto postV2Dto)) {
            log.warn("AOP: Method {} returned unsupported type {}",
                    joinPoint.getSignature().getName(),
                    result != null ? result.getClass().getSimpleName() : "null");
            return;
        }
        List<Long> subscribersIds = userServiceClient.getUserFollowers(postV2Dto.authorId());

        kafkaProducerPostService.sendPublishPostEvent(postV2Dto.id(), subscribersIds);
    }
}