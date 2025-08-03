package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlbumCreateDto {
    @NotNull(message = "Author id could not be null")
    private Long authorId;

    @NotBlank(message = "Album title could not be blank")
    private String title;

    @NotBlank(message = "Album description could not be blank")
    private String description;
}
