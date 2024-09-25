package faang.school.postservice.dto.album;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumFilterDto {
    String titlePattern;
    String descriptionPattern;
    LocalDateTime createdFromDate;
}
