package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedCommentDto {
    private Long id;
    private Long postId;
    private String content;
    private Long authorId;
    private long likes;
    private String createdAt;
    private String updatedAt;
}
