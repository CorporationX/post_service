package faang.school.postservice.dto.like;

import lombok.Data;

@Data
public class LikeDto {
    private Long id;
    private long userId;
    private long commentId;
    private long postId;
}
