package faang.school.postservice.model.dto.ad;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AdDto(
        @Positive(message = "Post id can not be < than 0")
        long postId,
        @Positive(message = "Buyer id can not be < than 0")
        long buyerId,
        long appearancesLeft,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price
) {
}
