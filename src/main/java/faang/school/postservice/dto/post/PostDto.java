package faang.school.postservice.dto.post;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 4096)
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Integer> likeIds;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime spellCheckedAt;
    private boolean published;
}