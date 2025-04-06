package faang.school.postservice.dto.album;

import lombok.Builder;

@Builder
public record AlbumDto(
        long id,
        String title,
        String description,
        long authorId
) {
}