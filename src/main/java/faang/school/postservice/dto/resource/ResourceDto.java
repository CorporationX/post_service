package faang.school.postservice.dto.resource;

import faang.school.postservice.messages.ValidationMessages;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceDto {
    @NotEmpty(message = ValidationMessages.VALIDATION_KEY_NOT_EMPTY)
    @Size(max = 50, message = ValidationMessages.VALIDATION_KEY_MAX_LENGTH)
    private String key;

    private long size;

    @NotEmpty(message = ValidationMessages.VALIDATION_NAME_NOT_EMPTY)
    @Size(max = 150, message = ValidationMessages.VALIDATION_NAME_MAX_LENGTH)
    private String name;

    @NotEmpty(message = ValidationMessages.VALIDATION_TYPE_NOT_EMPTY)
    @Size(max = 50, message = ValidationMessages.VALIDATION_TYPE_MAX_LENGTH)
    private String type;

    @NotNull(message = ValidationMessages.VALIDATION_POST_ID_NOT_NULL)
    private Long postId;
}
