package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.NotNull;

public enum Currency {
    @NotNull(message = "Валюта не может быть пустым полем.")
    USD,
    @NotNull(message = "Валюта не может быть пустым полем.")
    EUR
}
