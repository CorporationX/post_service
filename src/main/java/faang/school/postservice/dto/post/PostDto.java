package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        boolean published,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDateTime publishedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDateTime scheduledAt,
        boolean deleted,
        long likes
) {
}