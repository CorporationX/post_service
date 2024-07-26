package faang.school.postservice.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ListSplitter {
    public <T> List<List<T>> splitList(List<T> list, int size) {
        return IntStream.range(0, (list.size() + size - 1) / size)
                .mapToObj(i -> list.subList(i * size, Math.min(size * (i + 1), list.size())))
                .collect(Collectors.toList());
    }
}