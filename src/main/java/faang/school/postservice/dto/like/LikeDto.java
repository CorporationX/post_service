package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
    private Long likeId;
    private Long userId;
    private Long commentId;
    private Long postId;
}
