package faang.school.postservice.dto.album;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumVisibilityDto {
    private Long id;
    private Long authorId;
    private String email;
    private LocalDateTime time;
    private LocalDateTime updatedAt;
    private List<Long> visibilityUserId;
}
