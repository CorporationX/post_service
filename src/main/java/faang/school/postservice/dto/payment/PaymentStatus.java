package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.NotNull;

public enum PaymentStatus {
    @NotNull(message = "Статус платежа не может быть пустым.")
    SUCCESS
}
