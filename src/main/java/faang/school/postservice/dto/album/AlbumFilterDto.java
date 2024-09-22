package faang.school.postservice.dto.album;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlbumFilterDto {
    private String title;
    private Long authorId;
    private Integer minQuantityOfPosts;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private LocalDateTime updatedFrom;
    private LocalDateTime updatedTo;
}
