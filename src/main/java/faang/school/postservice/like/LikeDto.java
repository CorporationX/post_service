package faang.school.postservice.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Long id;
    @NonNull
    private Long userId;
    private Long postId;
    private Long commentId;
}
