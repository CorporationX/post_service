package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AlbumDTO {
    private Long id;
    @NotNull(message = "title must be filled")
    private String title;
    @NotNull(message = "description must be filled")
    private String description;
    @NotNull(message = "authorId must be filled")
    private long authorId;
    private List<Long> postIds = new ArrayList<>();
}