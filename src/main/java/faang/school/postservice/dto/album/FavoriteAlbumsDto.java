package faang.school.postservice.dto.album;

import java.time.LocalDateTime;

public record FavoriteAlbumsDto(
        long id,
        Long albumId,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}