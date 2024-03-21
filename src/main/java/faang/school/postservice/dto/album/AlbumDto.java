package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
