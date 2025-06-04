package faang.school.postservice.dto.album;

import faang.school.postservice.model.Post;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumDto {
    @Min(value = 1, message = "Id must be a positive number")
    private Long id;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 256, message = "Title must be no longer than 256 characters")
    private String title;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 256, message = "Description must be no longer than 4096 characters")
    private String description;

    @NotNull(message = "Author id must be a positive number")
    @Min(value = 1, message = "Author id must be a positive number")
    private Long authorId;
    private Post post;
}
