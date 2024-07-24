package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    private Long authorId;
    private Long projectId;

    @NotBlank(message = "Content can't be null or blank")
    @Size(min = 1, max = 4096)
    private String content;

    private boolean published;
    private boolean deleted;
}
