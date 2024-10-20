package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdBoughtEvent {
    private Long postId;
    private Long userId;
    private Double paymentAmount;
    private Integer adDuration;
    private LocalDateTime eventTimestamp;
}
