package faang.school.postservice.dto.album;

import faang.school.postservice.model.AlbumVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AlbumDto {
    private Long id;
    @NotBlank(message = "Title cannot be empty")
    private String title;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    private Long authorId;
    private AlbumVisibility visibility;
    private List<Long> selectedUsersIds;
    private List<Long> postIds;
}
