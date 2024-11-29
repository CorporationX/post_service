package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentNewsFeedDto {
    private Long id;
    private Long authorId;
    private Long postId;
    private String content;
    private List<Long> likes;
    private LocalDateTime createdAt;
}
