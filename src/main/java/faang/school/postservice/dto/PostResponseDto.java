package faang.school.postservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostResponseDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        Integer likeCount,
        List<Long> commentsId,
        List<Long> albumsId,
        Long adId,
        List<Long> resourcesId,
        boolean published,
        LocalDateTime publishedAt,
        List<Long> hashtagsId
) {
}
