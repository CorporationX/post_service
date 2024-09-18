package faang.school.postservice.dto.album;

import lombok.Data;

@Data
public class UpdateAlbumDto {
    private Long id;
    private String title;
    private String description;
}
