package faang.school.postservice.utils;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ListUtils {

    public static <T> Stream<List<T>> chunk(List<T> list, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size mut be positive value");
        }

        int size = list.size();

        return IntStream.range(0, (size + chunkSize - 1) / chunkSize)
                .mapToObj(i -> list.subList(i * chunkSize, Math.min(size, (i + 1) * chunkSize)));
    }
}
