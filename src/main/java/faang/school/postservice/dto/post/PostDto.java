package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Setter
@Getter
public class PostDto {
        private Long id;

        @NotBlank(message = "Title can not be null or empty")
        @Size(min = 1, max = 150)
        private String title;

        @NotBlank(message = "Content can not be null or empty")
        @Size(min = 1, max = 4096)
        private String content;

        private Long authorId;
        private Long projectId;
}
