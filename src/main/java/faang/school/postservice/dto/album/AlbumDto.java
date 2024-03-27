package faang.school.postservice.dto.album;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {
    @NotNull
    private Long id;
    @NotBlank(message = "Album must have a title")
    private String title;
    @NotBlank(message = "Album must have a description")
    private String description;
    @NotNull
    private long authorId;
    @NotEmpty
    private List<Long> postsIds;
}
