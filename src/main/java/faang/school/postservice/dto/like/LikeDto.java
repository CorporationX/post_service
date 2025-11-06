package faang.school.postservice.dto.like;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;

public record LikeDto(
        UserDto userDto,
        CommentDto commentDto,
        PostDto postDto
) {
}
