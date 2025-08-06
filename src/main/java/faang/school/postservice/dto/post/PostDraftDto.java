package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDraftDto {

    @NotBlank
    private String content;

    private Long authorId;

    private Long projectId;

}
