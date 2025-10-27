package faang.school.postservice.dto.album;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user_service.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {
    private long id;
    private String title;
    private String description;
    private UserDto author;
    private List<PostDto> posts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
