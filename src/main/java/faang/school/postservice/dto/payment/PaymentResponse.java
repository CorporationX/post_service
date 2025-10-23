package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentResponse(
        @NotNull(message = "Payment status cannot be empty")
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        @NotNull(message = "Payment amount cannot be empty")
        BigDecimal amount,
        @NotNull(message = "Currency cannot be empty")
        Currency currency,
        @NotBlank(message = "Response body cannot be empty")
        String message
) {
}
