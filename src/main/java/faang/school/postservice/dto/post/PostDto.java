package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private long id;
    @NotNull(message = "Пост не может быть пустым")
    @NotBlank(message = "Пост не может быть пустым")
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}
