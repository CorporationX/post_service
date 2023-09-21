package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostViewEventDto {
    private Long postId;
    private LocalDateTime viewDate;
}
