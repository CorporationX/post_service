package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;

@Data
@Builder
public class LikeDto {
    private long id;
    private Long commentId;
    private Long postId;
}
