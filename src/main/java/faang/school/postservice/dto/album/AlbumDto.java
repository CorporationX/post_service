package faang.school.postservice.dto.album;

import faang.school.postservice.model.Visibility;
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
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private List<Long> postIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Visibility visibility;
    private List<Long> allowedUsersIds;
}
