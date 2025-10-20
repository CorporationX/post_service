package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.comment.PostDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AlbumDto {
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    private List<PostDto> posts;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}