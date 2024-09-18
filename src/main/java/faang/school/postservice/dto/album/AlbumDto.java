package faang.school.postservice.dto.album;

import lombok.Data;

import java.util.List;

@Data
public class AlbumDto {
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private List<Long> postIds;
}
