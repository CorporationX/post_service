package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    @NotBlank(message = "Comment content can't be empty or blank")
    @NotNull(message = "Comment content can't be null")
    @Size(max = 4096, message = "Content must be less than 4096 characters")
    private String content;
    @NotNull(message = "Comment author ID cant be null")
    private Long authorId;
    @NotNull(message = "Comment post ID cant be null")
    private Long postId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @ReadOnlyProperty
    private LocalDateTime updatedAt;
}
