package faang.school.postservice.dto.album;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AlbumResponseDto {
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private List<Long> postIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
