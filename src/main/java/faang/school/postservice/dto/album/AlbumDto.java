package faang.school.postservice.dto.album;

import lombok.Data;

@Data
public class AlbumDto {
    private long id;
    private long authorId;
    private String title;
}
