package faang.school.postservice.dto;

import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 256)
    private String titlePattern;
    @Size(min = 1, max = 4096)
    private String descriptionPattern;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private List<Long> authorIdList;
}