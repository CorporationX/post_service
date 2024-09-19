package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCommentDto {
    @NotNull
    private Long id;
    @NotEmpty
    @Size(max = 4096)
    private String content;
}
