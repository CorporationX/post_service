package faang.school.postservice.dto.album;

import faang.school.postservice.model.album.AlbumVisibility;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AuthorAlbumDto extends AlbumDtoResponse{
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private List<Long> postsId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AlbumVisibility visibility;
    private List<Long> usersWithAccessIds;
}



