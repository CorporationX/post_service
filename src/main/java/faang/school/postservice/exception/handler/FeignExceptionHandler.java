package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.FeignClientException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignExceptionHandler {
    public RuntimeException handleFeignException(FeignException e, String entityName, Long entityId) {
        if (e instanceof FeignException.NotFound) {
            throw new EntityNotFoundException(
                    String.format("Entity %s with id: %d was not found. Problem: %s",
                            entityName,
                            entityId,
                            e.getMessage())
            );
        }
        throw new FeignClientException(
                String.format("Unknown problem getting %s id: %d from external service. Problem: %s",
                        entityName,
                        entityId,
                        e.getMessage())
        );
    }
}
