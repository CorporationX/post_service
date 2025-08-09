package faang.school.postservice.dto.feed;

public record UserFeedDto(
        Long userId,
        String username,
        String email,
        String fileId,
        String smallFileId
) {
}
