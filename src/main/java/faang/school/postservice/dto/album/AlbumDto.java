package faang.school.postservice.dto.album;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public record AlbumDto(
        String title,
        String description,
        UserDto authorDto,
        List<PostDto> postsDto
) {
}
