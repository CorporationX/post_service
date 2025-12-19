package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class PostDto {
        @NotBlank
        private String content;

        private Long id;
        private Long authorId;
        private Long projectId;
        private boolean published;
        private boolean deleted;
        private LocalDateTime publishedAt;
        private LocalDateTime createdAt;
}
