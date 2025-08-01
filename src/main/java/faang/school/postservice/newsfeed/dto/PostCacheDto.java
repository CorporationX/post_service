package faang.school.postservice.newsfeed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCacheDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Long likesAmount;
    private List<Long> commentIds;
    private List<Long> albumIds;
    private Long adId;
    private List<Long> resourceIds;
    private LocalDateTime publishedAt;
}
