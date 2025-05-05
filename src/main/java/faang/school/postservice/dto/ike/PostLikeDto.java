package faang.school.postservice.dto.ike;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeDto {
    private Long postId;
    private Long userId;
}
