package faang.school.postservice.dto.post;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDraftWithFilesCreateDto {
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> albumsId;

    @AssertTrue(message = "The author of a post can be either a user or a project")
    public boolean isAuthorOrProject() {
        return authorId != null && projectId == null || authorId == null && projectId != null;
    }
}
