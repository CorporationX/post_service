package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;

public record SortingStrategyDto(

        @NotNull(message = "Filed for sorting is required")
        SortingBy field,

        @NotNull(message = "Sorting order is required")
        SortingOrder order) {
}
