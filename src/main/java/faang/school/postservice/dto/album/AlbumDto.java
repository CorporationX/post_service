package faang.school.postservice.dto.album;

import faang.school.postservice.model.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AlbumDto {

    private long id;
    private String title;
    private String description;
    private long authorId;
    private List<Long> postsId;
    private Visibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
