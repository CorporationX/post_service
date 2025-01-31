package faang.school.postservice.dto.likes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeDto {
    private Long id;
    private Long userId;
    private Long commentId;
    private Long postId;
}
