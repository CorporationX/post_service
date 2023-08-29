package faang.school.postservice.dto.album;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@AllArgsConstructor
@NoArgsConstructor
public class AlbumDto {
    private Long id;
    @NotNull
    @NotBlank
    @Min(1)
    @Max(256)
    private String title;
    @NotNull
    @NotBlank
    @Min(1)
    @Max(4096)
    private String description;
    @NotNull(message = "AuthorId can't be null")
    private Long authorId;
    private List<Integer> postIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

