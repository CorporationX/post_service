package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumDto {
    private String title;
    private String description;
    private long authorId;
    private List<Long> postsIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
