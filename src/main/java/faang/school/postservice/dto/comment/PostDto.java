package faang.school.postservice.dto.comment;

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
public class PostDto {
    private Long id;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 4096, message = "Content cannot exceed 4096 characters")
    private String content;

    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    private Long projectId;

    private List<AlbumDto> albums;
    private AdDto ad;
    private List<ResourceDto> resources;

    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}