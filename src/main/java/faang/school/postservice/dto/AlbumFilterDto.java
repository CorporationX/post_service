package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumFilterDto {
    private String titlePattern;
    private String descriptionPattern;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private List<Long> authorIdList;
}