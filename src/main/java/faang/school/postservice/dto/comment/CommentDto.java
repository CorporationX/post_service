package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = "Content cannot be empty!")
    @Size(min = 1, max = 4096, message = "Contest size should be between 1 and 4096!")
    private String content;

    @NotNull
    @Min(value = 1L, message = "Author id cannot be lower than 1!")
    private Long authorId;

    @NotNull
    @Min(value = 1L, message = "Post id cannot be lower than 1!")
    private Long postId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}