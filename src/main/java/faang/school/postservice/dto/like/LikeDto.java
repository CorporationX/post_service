package faang.school.postservice.dto.like;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LikeDto implements Serializable {
    private Long id;
    private Long postId;
    private Long commentId;
    private Long userId;
}