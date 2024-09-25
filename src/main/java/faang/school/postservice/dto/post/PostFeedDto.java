package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentFeedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostFeedDto {

    private Long id;

    private String content;

    private String authorName;

    private List<CommentFeedDto> lastComments;

    private int commentsAmount;

    private long likesAmount;

    private LocalDateTime publishedAt;
}