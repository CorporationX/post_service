package faang.school.postservice.dto.post;

import faang.school.postservice.model.post.PostLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateProjectRequestDto {
    @NotBlank(message = "Content is mandatory")
    @Size(max = 4096, message = "Max length content — 4096 char")
    private String content;
    @NotBlank(message = "Language is mandatory")
    private PostLanguage language;
    @NotNull(message = "ProjectId is mandatory")
    private Long projectId;
}
