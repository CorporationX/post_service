package faang.school.postservice.model.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AdBoughtEvent(
        long postId,
        long userId,
        BigDecimal paymentAmount,
        int adDuration,
        LocalDateTime boughtAt
) {
}
