package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedPostDto {
    private Long id;
    private Long authorId;
    private Long projectId;
    private String authorName;
    private String projectName;
    private int views;
    private int likes;
    private int comments;
    private String createdAt;
    private String updatedAt;
    private String content;
}
