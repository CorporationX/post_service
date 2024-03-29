package faang.school.postservice.dto.album;

import faang.school.postservice.model.AlbumVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {
    private Long id;
    @NotBlank(message = "Album must have a title")
    private String title;
    @NotBlank(message = "Album must have a description")
    private String description;
    private long authorId;
    private List<Long> postsIds;
    private AlbumVisibility albumVisibility;
}
