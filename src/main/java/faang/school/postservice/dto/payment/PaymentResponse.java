package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentResponse(
        @NotNull(message = "Статус платежа не может быть пустым.")
        PaymentStatus status,
        @NotNull(message = "Код проверки не может быть пустым.")
        int verificationCode,
        @NotNull(message = "Номер платежа не может быть пустым.")
        long paymentNumber,
        @Min(1)
        @NotNull(message = "Количество не может быть пустым.")
        BigDecimal amount,
        @NotNull(message = "Валюта не может быть пустым полем.")
        Currency currency,
        @NotBlank(message = "Сообщение не может быть пустым, или иметь одни пробелы.")
        String message
) {
}
