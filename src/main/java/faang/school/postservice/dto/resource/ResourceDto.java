package faang.school.postservice.dto.resource;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDto {
    @Min(value = 1, message = "ID cannot be less than or equal to 0")
    private Long id;
    private String key;
    private long size;
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotNull
    private Long postId;
}
