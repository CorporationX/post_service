package faang.school.postservice.dto.album;

import faang.school.postservice.model.UserVisibility;
import faang.school.postservice.model.VisibilityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumDto {
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private List<Long> postsId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private VisibilityType visibilityType;
    private List<Long> visibilityUsersId;
}