package faang.school.postservice.dto.album;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumFilterDto {
    private String titlePattern;
    private String descriptionPattern;
    private long authorIdPattern;
    private LocalDateTime createdAtPattern;
    private LocalDateTime updatedAtPattern;

}
