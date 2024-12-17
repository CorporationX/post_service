package faang.school.postservice.dto.album;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AlbumDto {

    private long id;
    @NotBlank(message = "Имя не должно быть пустым.")
    private String title;
    @NotBlank(message = "Описание не должно быть пустым.")
    private String description;
    @Min(value = 1, message = "Некорректный ID автора")
    private long authorId;

}
