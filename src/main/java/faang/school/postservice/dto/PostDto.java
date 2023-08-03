package faang.school.postservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostDto {
    @Min(1)
    private long id;
    @NotBlank
    private String content;
    private Long authorId;
    private Long projectId;
}
