package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumUpdateDto {
    private Long albumId;

    @NotNull
    private Long authorId;

    private String title;
    private String description;
    private List<Long> postsIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
