package faang.school.postservice.dto.add;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

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
