package faang.school.postservice.dto.album;

import faang.school.postservice.model.Post;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AlbumDto {
    private String title;
    private String description;
    private long authorId;
    private List<Post> posts;
}