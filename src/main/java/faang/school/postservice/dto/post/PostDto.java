package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PostDto {
    private Long id;
    @NotNull
    @NotBlank
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private boolean published;
    private boolean deleted;
    @Size(max = 10)
    private List<Long> resourceIds;
}
