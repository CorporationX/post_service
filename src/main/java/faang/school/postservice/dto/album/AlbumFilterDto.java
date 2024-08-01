package faang.school.postservice.dto.album;

import faang.school.postservice.model.VisibilityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumFilterDto {
    private String title;
    private String description;
    private Long authorId;
    private VisibilityType visibilityType;
}