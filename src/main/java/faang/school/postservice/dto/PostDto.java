package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Builder
public record PostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        List<Long> likesId,
        Integer likeCount,
        List<Long> commentsId,
        List<Long> albumsId,
        Long adId,
        List<Long> resourcesId,
        boolean published,
        LocalDateTime publishedAt,
        LocalDateTime scheduledAt,
        boolean deleted,
        List<String> hashtagsName
) {
}
