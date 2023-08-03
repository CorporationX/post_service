package faang.school.postservice.dto.post;

import faang.school.postservice.model.Like;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Like> likes;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
}