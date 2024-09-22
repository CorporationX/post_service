package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlbumDto {
    @NotNull(message = "Title can`t be null")
    private String title;
    private String description;
}
