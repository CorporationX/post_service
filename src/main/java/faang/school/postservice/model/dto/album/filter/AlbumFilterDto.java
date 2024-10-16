package faang.school.postservice.model.dto.album.filter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AlbumFilterDto {
    private String title;
    private String description;
    private Long authorId;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
}
