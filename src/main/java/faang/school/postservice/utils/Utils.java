package faang.school.postservice.utils;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.IntStream;

@UtilityClass
public class Utils {
    public static <T> List<List<T>> chunked(List<T> list, int chunk) {
        if (chunk <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }
        int parts = (list.size() + chunk - 1) / chunk;
        return IntStream.range(0, parts)
                .mapToObj(i -> list.subList(i * chunk, Math.min((i + 1) * chunk, list.size())))
                .toList();
    }
}