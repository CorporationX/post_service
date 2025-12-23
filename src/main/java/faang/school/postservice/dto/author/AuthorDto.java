package faang.school.postservice.dto.author;

import lombok.Builder;

import java.time.Duration;

@Builder
public record AuthorDto (
        String id,
        String username,
        String avatarUrl,
        Duration ttl
){
}
