package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AlbumDto extends AlbumDtoResponse {
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private List<Long> postsId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
