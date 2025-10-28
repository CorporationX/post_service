package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record PostDto(Long id,
                      String content,
                      Long authorId,
                      Long projectId,
                      List<Long> likesIds,
                      List<Long> commentsIds,
                      List<Long> albumsIds,
                      Long adId,
                      List<Long> resourcesIds,
                      boolean published,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                      LocalDateTime publishedAt,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                      LocalDateTime scheduledAt,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                      LocalDateTime createdAt,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                      LocalDateTime updatedAt
) {
}
