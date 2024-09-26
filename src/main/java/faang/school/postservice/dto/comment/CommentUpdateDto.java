package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentUpdateDto {
    private Long id;

    @Size(min = 1, max = 4096)
    @NotBlank(message = "Content cannot be blank")
    private String content;

    private Long authorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
