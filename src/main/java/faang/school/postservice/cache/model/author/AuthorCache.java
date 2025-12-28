package faang.school.postservice.cache.model.author;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@Builder
@FieldNameConstants
public record AuthorCache(
        Long userId,
        String username
) {
}
