package faang.school.postservice.model.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisKeyConstants {

    FEED_KEY_PREFIX("feed:"),
    POST_KEY_PREFIX("post:"),
    LIKES_SUFFIX(":likes"),
    VIEWS_SUFFIX(":views"),
    COMMENTS_SUFFIX(":comments"),
    PROCESSED_PREFIX("processed:");

    private final String value;
}
