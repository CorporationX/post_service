package faang.school.postservice.dto.posts;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    private Long id;
    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedDate;
    private Boolean published;
    private String content;
    private int likeCount;
}
