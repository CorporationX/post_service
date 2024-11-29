package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDtoForNewsFeed {
    private Long postId;
    private Long authorId;
    private String content;
    private Integer countLikes;
}
