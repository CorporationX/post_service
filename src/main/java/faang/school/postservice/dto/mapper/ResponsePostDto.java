package faang.school.postservice.dto.mapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePostDto {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likesIds;
    private List<Long> commentsIds;
    private List<Long> albumsIds;
    private Long adId;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
