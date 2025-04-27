package faang.school.postservice.event;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostViewEvent {

    @NotNull
    private Long postId;

    @NotNull
    private Long viewerId;

    @NotNull
    private LocalDateTime timestamp;
}
