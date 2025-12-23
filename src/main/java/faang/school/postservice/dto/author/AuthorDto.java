package faang.school.postservice.dto.author;

import lombok.Builder;

@Builder
public record AuthorDto (
        String id,
        String username,
        String avatarUrl
){
}
