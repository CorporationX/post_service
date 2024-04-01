package faang.school.postservice.dto.album;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumUpdateDto {
    private String title;
    private String description;
    private long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
