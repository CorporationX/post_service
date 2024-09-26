package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AlbumDto {

    @Positive(message = "Id can't be zero or negative")
    private Long id;

    @NotNull(message = "authorId can't be null")
    @Positive(message = "authorId can't be zero or negative")
    private Long authorId;

    @NotBlank(message = "Title can't be empty or null")
    private String title;

    private String description;

    private List<Long> postIds;
}