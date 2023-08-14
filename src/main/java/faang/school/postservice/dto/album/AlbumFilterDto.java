package faang.school.postservice.dto.album;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlbumFilterDto {
    private String title;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
