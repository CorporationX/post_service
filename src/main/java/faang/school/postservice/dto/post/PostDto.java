package faang.school.postservice.dto.post;

import faang.school.postservice.model.Like;
import faang.school.postservice.enums.AuthorType;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {

    private Long id;
    private List<Like> likes;
    private LocalDateTime publishedAt;
    
    @NotNull(message = "Content must not be null")
    @NotBlank(message = "Content must not be blank")
    @Size(max = 4096, message = "The content must not exceed 4096 characters")
    private String content;

    @NotNull(message = "Author ID must be provided")
    private Long authorId;

    @NotNull(message = "Author type must be provided")
    private AuthorType authorType;

    private boolean published;
    private LocalDateTime publishedAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}