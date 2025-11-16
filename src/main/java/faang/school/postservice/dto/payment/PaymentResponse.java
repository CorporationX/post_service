package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentResponse(
        @NotBlank(message = "status should not be blank")
        PaymentStatus status,
        @Min(value = 0, message = "verificationCode should be grater than 0")
        int verificationCode,
        @Min(value = 0, message = "verificationCode should be grater than 0")
        long paymentNumber,
        @Min(value = 1, message = "amount should be grater than 1")
        BigDecimal amount,
        @NotNull(message = "currency should not be null")
        Currency currency,
        @NotBlank(message = "message should not be blank")
        String message
) {
}
