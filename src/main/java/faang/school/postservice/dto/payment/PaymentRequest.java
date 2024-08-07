package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Номер не может быть пустым.")
        @Min(1)
        long paymentNumber,

        @Min(1)
        @NotNull(message = "Количество не может быть пустым.")
        BigDecimal amount,

        @NotNull(message = "Валюта не может быть пустым полем.")
        Currency currency
) {
}
