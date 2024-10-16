package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumCreatedEvent {
    private Long userId;
    private Long albumId;
    private String albumName;
    private LocalDateTime eventTime;
}
