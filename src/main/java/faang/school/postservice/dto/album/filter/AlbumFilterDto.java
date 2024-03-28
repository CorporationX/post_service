package faang.school.postservice.dto.album.filter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumFilterDto {
    @Size(max = 150, message = "Event title can't be longer than 150 characters")
    private String title;
}
