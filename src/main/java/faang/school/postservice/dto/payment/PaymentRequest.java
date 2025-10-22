package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Payment number cannot be null")
        long paymentNumber,
        @Min(value = 1, message = "Amount must be at least 1")
        @NotNull(message = "Amount cannot be null")
        BigDecimal amount,
        @NotNull(message = "Currency cannot be null")
        Currency currency
) {
}
