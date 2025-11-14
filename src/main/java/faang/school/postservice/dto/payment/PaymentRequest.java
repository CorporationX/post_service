package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "paymentNumber should not be null")
        long paymentNumber,

        @Min(value = 1, message = "amount should be grater than 1")
        @NotNull(message = "amount should not be null")
        BigDecimal amount,

        @NotNull(message = "currency should not be null")
        Currency currency
) {
}
