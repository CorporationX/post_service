package faang.school.postservice.utils;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

@Getter
public enum ImageRestrictionRule {
    POST_IMAGES(5 * 1024 * 1024,
        Pair.of(1080, 1080), Pair.of(1080, 566), Pair.of(1080, 566));

    private final long maxSize;
    private final Pair<Integer, Integer> square;
    private final Pair<Integer, Integer> horizontal;
    private final Pair<Integer, Integer> vertical;

    ImageRestrictionRule(long maxSize, Pair<Integer, Integer> square,
                         Pair<Integer, Integer> horizontal, Pair<Integer, Integer> vertical) {
        this.maxSize = maxSize;
        this.square = square;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }
}
