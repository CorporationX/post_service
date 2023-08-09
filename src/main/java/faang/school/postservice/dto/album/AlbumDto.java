package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumDto {
    @NotBlank(message = "Album title is required")
    private String title;
    @NotBlank(message = "Album description is required")
    private String description;
    private long authorId;
    private List<Long> postsIds;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
}
