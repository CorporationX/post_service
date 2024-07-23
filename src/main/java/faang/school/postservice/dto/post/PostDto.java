package faang.school.postservice.dto.post;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Long id;

    @Positive
    private Long authorId;

    @Positive
    private Long projectId;

    @NotBlank(message = "Content can't be null or blank")
    @Max(4096)
    private String content;

    private boolean published;
    private boolean deleted;
}
