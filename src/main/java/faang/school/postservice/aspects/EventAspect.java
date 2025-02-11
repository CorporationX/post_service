package faang.school.postservice.aspects;

import faang.school.postservice.annotations.PublishEvent;
import faang.school.postservice.event.Event;
import faang.school.postservice.publisher.AbstractEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class EventAspect {

    private final ApplicationContext context;

    @AfterReturning(pointcut = "@annotation(publishEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, PublishEvent publishEvent, Object result) {
        if (result == null) {
            log.info("Method returned {} null, event will not be published.", joinPoint.getSignature().getName());
            return;
        }

        try {
            AbstractEventPublisher publisherBean = context.getBean(publishEvent.publisher());
            Object mapperBean = context.getBean(publishEvent.mapper());
            Method mappingMethod = publishEvent.mapper().getMethod(publishEvent.mappingMethod(), result.getClass());
            Event event = (Event) mappingMethod.invoke(mapperBean, result);
            publisherBean.publishEvent(event);
        } catch (Exception e) {
            log.error("Publish event exception: {}", e);
        }
    }
}
