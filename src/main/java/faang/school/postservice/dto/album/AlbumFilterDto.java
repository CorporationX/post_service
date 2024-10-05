package faang.school.postservice.dto.album;

import faang.school.postservice.model.album.AlbumVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumFilterDto {
    private String title;
    private Long authorId;
    private Integer minQuantityOfPosts;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private LocalDateTime updatedFrom;
    private LocalDateTime updatedTo;
    private AlbumVisibility visibility;
}
