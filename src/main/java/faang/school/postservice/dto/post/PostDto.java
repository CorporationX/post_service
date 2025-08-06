package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostDto {

    @NotBlank
    private String content;

    private Long authorId;

    private Long projectId;

    private Long id;

    private boolean deleted;

    private boolean published;

    private LocalDateTime publishedAt;
}
