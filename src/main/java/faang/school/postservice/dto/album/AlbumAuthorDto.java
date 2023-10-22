package faang.school.postservice.dto.album;

import faang.school.postservice.model.AlbumVisibility;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AlbumAuthorDto extends AlbumDtoResponse{
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AlbumVisibility visibility;
    private List<Long> idUsersWithAccess;
}
