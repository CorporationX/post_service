package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumLightDto {
    private Long id;
    private String title;
    private String description;
}