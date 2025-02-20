package faang.school.postservice.dto.Post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private long id;
    private String content;
    private long authorId;
    private long projectId;
    private List<Long> likesIds;
    private List<Long> commentsIds;
    private long adId;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    public boolean deleted;
    private int likesCount;
}