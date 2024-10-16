package faang.school.postservice.model.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull
        long paymentNumber,

        @Positive
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}
