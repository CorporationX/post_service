package faang.school.postservice.dto.post;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequestDto {
    @NotBlank(message = "Content is mandatory")
    @Size(max = 4096, message = "Max length content — 4096 char")
    private String content;
    private Long authorId;
    private Long projectId;

    @AssertTrue(message = "Only one of authorId or projectId must be specified")
    public boolean isOnlyOneIdPresent() {
        return (authorId != null && projectId == null) ||
                (authorId == null && projectId != null);
    }
}
