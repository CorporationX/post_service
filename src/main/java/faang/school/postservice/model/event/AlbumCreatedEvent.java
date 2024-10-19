package faang.school.postservice.model.event;

import lombok.Builder;

@Builder
public record AlbumCreatedEvent(
        long userId,
        long albumId,
        String albumName
) {
}
