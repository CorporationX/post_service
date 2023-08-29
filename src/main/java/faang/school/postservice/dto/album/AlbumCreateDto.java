package faang.school.postservice.dto.album;

import faang.school.postservice.model.AlbumVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumCreateDto {
    @NotBlank
    @Size(max = 127,message = "Title must be less than 127 characters")
    private String title;
    @NotBlank
    @Size(max = 4096,message = "Description must be less than 4096 characters")
    private String description;
    @NotNull
    private Long authorId;
    private AlbumVisibility visibility;
    private List<Long> usersWithAccessIds;
}
