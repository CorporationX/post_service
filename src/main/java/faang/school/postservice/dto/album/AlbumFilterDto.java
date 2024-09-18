package faang.school.postservice.dto.album;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AlbumFilterDto {
    private String titlePattern;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime afterThisTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime beforeThisTime;
}
