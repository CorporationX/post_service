package faang.school.postservice.model.response;

import faang.school.postservice.model.enums.Currency;
import faang.school.postservice.model.enums.PaymentStatus;

import java.math.BigDecimal;
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
