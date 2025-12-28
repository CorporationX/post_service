package faang.school.postservice.feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.user.UserDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostFeedItemDto(
        Long id,
        String content,
        UserDto author,
        Long likesCount,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {
}
