package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AlbumUpdateDto {
    private String title;
    private String description;
    @NotNull
    private Long authorId;
    private List<Long> postsId;
    private LocalDateTime updatedAt;
}
