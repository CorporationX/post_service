package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
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
public class PostFeedResponse {
    private Long id;
    private String content;
    private Long authorId;
    private Integer likeCount;
    private Long viewCount;
    private List<CommentDto> comments;
    private UserDto author;
    private LocalDateTime publishedAt;
}
