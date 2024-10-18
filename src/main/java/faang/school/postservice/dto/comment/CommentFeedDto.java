package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.user.UserFeedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentFeedDto {
    private Long id;
    private String content;
    private UserFeedDto author;
    private List<Long> likesId;
    private Long likeCount;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
