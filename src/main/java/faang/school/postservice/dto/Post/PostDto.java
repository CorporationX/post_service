package faang.school.postservice.dto.Post;

import faang.school.postservice.dto.HashTag.HashTagDto;
import faang.school.postservice.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDto(
        Long id,
        String content,
        UserDto author,
        Long countLikes,
        List<HashTagDto> hashTags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
