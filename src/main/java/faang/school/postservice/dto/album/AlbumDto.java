package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlbumDto {
    private long id;
    private long authorId;
    private String title;
}
