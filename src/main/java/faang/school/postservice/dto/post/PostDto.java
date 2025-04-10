package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    @NotBlank(message = "Content can not be blank or null")
    private String content;
    @Min(value = 1, message = "authorId cannot be less than 1")
    private Long authorId;
    @Min(value = 1, message = "projectId cannot be less than 1")
    private Long projectId;
    private boolean published;
    private boolean deleted;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Long> resourceIds;
}