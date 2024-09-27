package faang.school.postservice.dto.album;

import faang.school.postservice.model.album.AlbumVisibility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlbumDto {
    @NotNull(message = "Title can`t be null")
    @Size(max = 256, message = "")
    private String title;
    @NotNull(message = "Description can`t be null")
    @Size(max = 4096, message = "")
    private String description;
    @NotNull(message = "Visibility cannot be null")
    private AlbumVisibility visibility;
    private List<Long> chosenUserIds;
}
