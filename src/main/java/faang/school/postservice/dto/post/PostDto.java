package faang.school.postservice.dto.post;

import faang.school.postservice.dto.user.UserDto;

import java.time.LocalDateTime;

public record PostDto(
        Long id,
        String content,
        UserDto author,
        int likesCount,
        int commentsCount,
        int viewsCount,
        LocalDateTime publishedAt,
        LocalDateTime updatedAt
) {
}
