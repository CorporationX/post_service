package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AlbumDto {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private long authorId;
    private List<Long> postsIds;
}
