package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {
    private Long id;
    private Long postId;
    private Long commentId;
    private Long userId;
    private LocalDateTime createdAt;
}
