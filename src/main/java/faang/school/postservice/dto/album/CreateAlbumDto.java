package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 256, message = "")
    private String title;
    @NotNull(message = "Description can`t be null")
    @Size(max = 4096, message = "")
    private String description;
}
