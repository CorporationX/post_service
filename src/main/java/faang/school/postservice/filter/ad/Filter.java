package faang.school.postservice.filter.ad;

import java.util.stream.Stream;

public interface Filter<E> {
    Stream<E> apply(Stream<E> e);
}
