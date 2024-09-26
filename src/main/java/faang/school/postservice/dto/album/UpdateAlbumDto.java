package faang.school.postservice.dto.album;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAlbumDto {
    private Long id;
    private String title;
    private String description;
}
