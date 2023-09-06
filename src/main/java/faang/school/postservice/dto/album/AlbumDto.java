package faang.school.postservice.dto.album;

import faang.school.postservice.model.Post;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AlbumDto {
    @NotNull
    private String title;
    @NotNull
    private String description;
    private long authorId;
    private List<Post> posts;
}