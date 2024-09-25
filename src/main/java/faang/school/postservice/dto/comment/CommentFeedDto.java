package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentFeedDto {

    private Long id;

    private String content;

    private String authorName;

    private Long postId;

    private Long likeAmount;

    private LocalDateTime createdAt;
}