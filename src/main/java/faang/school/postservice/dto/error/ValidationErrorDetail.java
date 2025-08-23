package faang.school.postservice.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details about a specific validation error")
public record ValidationErrorDetail(

        @Schema(description = "Name of the field that failed validation")
        String field,

        @Schema(description = "Validation error comment")
        String message,

        @Schema(description = "The invalid value that was provided")
        Object rejectedValue
) {
}
