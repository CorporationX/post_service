package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class LikeResponseDto {
    private long id;

    private long userId;

    private Long postId;

    private Long commentId;

    private LocalDateTime createdAt;
}
