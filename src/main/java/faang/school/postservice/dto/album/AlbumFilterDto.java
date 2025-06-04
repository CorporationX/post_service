package faang.school.postservice.dto.album;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AlbumFilterDto {

    private String titlePattern;
    private LocalDateTime createdBeforeDate;
    private LocalDateTime createdAfterDate;
}