package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumDto {
    private Long id;
    @NonNull
    @NotBlank(message = "Title is required")
    private String title;
    @NonNull
    @NotBlank(message = "Description is required")
    private String description;
    private Long authorId;
    private List<Long> postIds;
    private LocalDateTime createdAt;
}
