package faang.school.postservice.dto;

import faang.school.postservice.dto.album.AlbumDto;

public record FavoriteAlbumDto(
        long id,
        long userId,
        AlbumDto albumDto
) {
}
