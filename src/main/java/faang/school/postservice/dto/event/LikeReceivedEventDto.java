package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeReceivedEventDto {
    private long receiverId;
    private long actorId;
    private String eventType;
    private String receivedAt;
}
