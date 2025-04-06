package faang.school.postservice.dto.album;

public record PostAlbumDto(
        long id,
        long postId,
        long albumId,
        long userId
) {
}
