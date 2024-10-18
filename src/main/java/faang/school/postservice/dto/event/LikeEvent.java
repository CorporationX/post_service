package faang.school.postservice.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeEvent {

    private Long id;
    @NotNull
    private Long userId;
    private Long commentId;
    private Long postId;
    private EventType eventType;

    public static enum EventType {
        CREATE, DELETE
    }
}
