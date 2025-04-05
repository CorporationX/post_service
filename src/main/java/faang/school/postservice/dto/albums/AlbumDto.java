package faang.school.postservice.dto.albums;

import jakarta.validation.constraints.NotBlank;
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
    private Long authorId;
    private List<Long> postIds = new ArrayList<>();
}
