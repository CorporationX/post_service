package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private Long authorId;
    private Long projectId;

    @NotBlank
    @Size(min = 1, max = 4000)
    private String content;

    @NotNull
    @Past
    private LocalDateTime publishedAt;

    @NotNull
    @Past
    private LocalDateTime createdAt;

    @NotNull
    @Past
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean published;

}
