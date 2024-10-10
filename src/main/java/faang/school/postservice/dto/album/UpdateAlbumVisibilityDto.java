package faang.school.postservice.dto.album;

import faang.school.postservice.model.album.AlbumVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateAlbumVisibilityDto {
    private Long id;
    @NotNull(message = "Visibility cannot be null")
    private AlbumVisibility visibility;
    private List<Long> chosenUserIds;
}
