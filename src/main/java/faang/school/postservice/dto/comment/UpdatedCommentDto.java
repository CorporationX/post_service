package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedCommentDto {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    @Positive
    private Long authorId;

    @Size(max = 4096)
    @NotBlank
    private String content;
}
