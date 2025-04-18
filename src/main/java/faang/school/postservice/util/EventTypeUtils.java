package faang.school.postservice.util;

import faang.school.postservice.model.event.Event;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EventTypeUtils {
    public static Class<? extends Event> validateAndCast(Class<?> eventType) {
        if (!Event.class.isAssignableFrom(eventType)) {
            throw new IllegalStateException("Invalid event type: " + eventType);
        }
        @SuppressWarnings("unchecked")
        Class<? extends Event> castedType = (Class<? extends Event>) eventType;
        return castedType;
    }

    public static <T> Map<Class<? extends Event>, T> createEventMap(
            List<T> items,
            Function<T, Class<?>> typeExtractor
    ) {
        return items.stream()
                .collect(Collectors.toMap(
                        item -> validateAndCast(typeExtractor.apply(item)),
                        Function.identity()
                ));
    }
}
