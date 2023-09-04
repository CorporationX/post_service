package faang.school.postservice.dto.album;

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
@AllArgsConstructor
@NoArgsConstructor
public class AlbumDto {
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 256)
    private String title;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 4096)
    private String description;
    @NotNull(message = "AuthorId can't be null")
    private Long authorId;
    private List<Integer> postIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

