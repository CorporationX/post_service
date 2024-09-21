package faang.school.postservice.dto.album;

import faang.school.postservice.model.AlbumVisibility;
import jakarta.validation.constraints.NotNull;
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
    private String title;
    private String description;
    @NotNull(message = "Visibility cannot be null")
    private AlbumVisibility visibility;
    private List<Long> chosenUserIds;
}
