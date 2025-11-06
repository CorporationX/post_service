package faang.school.postservice.dto.resource;


import faang.school.postservice.dto.post.PostDto;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public record ResourceDto(
        String key,
        long size,
        String name,
        String type,
        PostDto post,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
