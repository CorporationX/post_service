package faang.school.postservice.dto.author;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@Builder
@FieldNameConstants
public record AuthorDto(
        Long userId,
        String username
) {
}
