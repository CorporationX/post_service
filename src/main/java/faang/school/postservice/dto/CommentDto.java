package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentDto {

    private long id;

    @Size(min = 1, max = 4096, message = "Comment content cannot exceed 4096 characters")
    @NotBlank(message = "Comment content cannot be empty")
    private String content;

    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    @NotNull(message = "Post ID cannot be null")
    private Long postId;

    private LocalDateTime createdAt;
}