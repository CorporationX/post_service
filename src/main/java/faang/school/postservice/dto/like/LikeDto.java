package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;

@Data
@Builder
public class LikeDto {
    @Min(0)
    private long id;
    @NonNull
    private Long userId;
    private Long commentId;
    private Long postId;
}
