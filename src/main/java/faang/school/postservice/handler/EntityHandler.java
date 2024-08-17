package faang.school.postservice.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@Slf4j
public class EntityHandler {

    public <T> T getOrThrowException(Class<T> entityClass, long entityId, Supplier<Optional<T>> finder) {
        return finder.get().orElseThrow(() -> {
            String errMessage = String.format("Could not find %s with ID: %d", entityClass.getName(), entityId);
            EntityNotFoundException exception = new EntityNotFoundException(errMessage);
            log.error(errMessage, exception);
            return exception;
        });
    }

    public <S, T> void updateNonNullFields(S source, T target) {
        Field[] fields = source.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = ReflectionUtils.getField(field, source);

            if (value != null) {
                Field targetField = ReflectionUtils.findField(target.getClass(), field.getName());

                if (targetField != null) {
                    targetField.setAccessible(true);
                    ReflectionUtils.setField(targetField, target, value);
                }
            }
        }
    }
}
