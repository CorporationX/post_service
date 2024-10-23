package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdBoughtEvent {
    private long postId;
    private long userId;
    private int paymentAmount;
    private int adDuration;
    private LocalDateTime timestamp;
}
