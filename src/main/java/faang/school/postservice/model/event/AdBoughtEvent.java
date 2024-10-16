package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdBoughtEvent {
    private Long postId;
    private Long userId;
    private Long amount;
    private Integer advDuration;
    private LocalDateTime receivedAt;
}
