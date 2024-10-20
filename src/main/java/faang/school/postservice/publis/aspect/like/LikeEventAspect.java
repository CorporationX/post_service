package faang.school.postservice.publis.aspect.like;

import faang.school.postservice.model.Like;
import faang.school.postservice.publis.publisher.like.LikeEventPublisher;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class LikeEventAspect {
    private final LikeEventPublisher likeEventPublisher;


    @Around("@annotation(faang.school.postservice.annotation.like.PublishLikeEvent)")
    public Object publishLikeEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[args.length - 1] instanceof Like) {
            Like newLike = (Like) args[args.length - 1];
            likeEventPublisher.publishPostLikeEventToBroker(newLike);
        }

        return result;
    }
}
