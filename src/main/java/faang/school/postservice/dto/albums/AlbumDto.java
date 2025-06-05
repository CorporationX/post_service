package faang.school.postservice.dto.albums;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
public class AlbumDto {
    private Long id;
    @NotBlank(message = "Требуется название альбома")
    private String title;
    @NotBlank(message = "Требуется описание альбома")
    private String description;
    @Null(message = "authorId устанавливается автоматически и не должен передаваться вручную")
    private Long authorId;
    private List<Long> postIds = new ArrayList<>();
}
