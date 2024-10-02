package faang.school.postservice.dto.album;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
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
