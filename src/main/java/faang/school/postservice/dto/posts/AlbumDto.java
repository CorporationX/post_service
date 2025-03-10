package faang.school.postservice.dto.posts;

import lombok.Data;

@Data
public class AlbumDto {
    private Long id;
    private String title;
    private String description;
    private Long authorId;
}
