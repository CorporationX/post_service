package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {
    @NotBlank(message = "Content can't be cannot be empty")
    @Size(min = 1, max = 4096, message = "Content should be at least 1 symbol long and max 4096 symbols")
    private String content;
    private Long authorId;
    private Long projectId;
}
