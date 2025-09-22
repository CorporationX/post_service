package faang.school.postservice.dto.cache;

public record AuthorCacheDto(
        Long id,
        String username,
        String displayName,
        String avatarUrl,
        Boolean verified
) {
}
