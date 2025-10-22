package faang.school.postservice.dto.album;

import java.util.List;

public record AlbumDto(
        Long id,
        String title,
        String description,
        Long authorId,
        List<Long> postIds
) {
}