package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;

    private Boolean published;
    private Boolean deleted;

    private Long authorId;
    private Long projectId;

    @NotBlank(message = "Post content is required")
    @Length(max = 4096, message = "Post content can not be longer than 4096 characters")
    private String content;

    private Long likesCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
}
