package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotNull
    private Long id;
    @NotBlank(message = "Comment must have a content")
    @Size(max = 4096, message = "Comment length must be less than 4096 symbols")
    private String content;
    @NotNull
    private Long authorId;
    @NotNull
    private Long postId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdAt;
}
