package faang.school.postservice.util;

import java.util.Objects;
import java.util.function.Consumer;

public class ValidationUtils {

    public static <T> void setIfNotNull(T fieldValue, Consumer<T> setter) {
        if (Objects.nonNull(fieldValue)) {
            setter.accept(fieldValue);
        }
    }

    public static void executeIfNotNull(Object field, Runnable runnable) {
        if (Objects.nonNull(field)) {
            runnable.run();
        }
    }
}
