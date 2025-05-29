package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Builder
@Data
public final class PostResponseDto {
    private final Long id;
    private final String content;
    private final Long authorId;
    private final Long projectId;
    private final Integer likeCount;
    private final Long viewCount;
    private final List<Long> commentsId;
    private final List<Long> albumsId;
    private final Long adId;
    private final List<Long> resourcesId;
    private final boolean published;
    private final LocalDateTime publishedAt;
    private List<Long> hashtagsId;
}
