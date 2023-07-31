package faang.school.postservice.dto.album;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AlbumDto {
    private long id;
    private String title;
    private String description;
    private long authorId;
    private List<Long> postsIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
