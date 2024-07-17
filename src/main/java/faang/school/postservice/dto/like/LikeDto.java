package faang.school.postservice.dto.like;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LikeDto {
    private Long id;
    private Long postId;
    private Long commentId;
    private Long userId;
}