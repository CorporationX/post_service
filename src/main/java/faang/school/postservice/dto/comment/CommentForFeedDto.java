package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentForFeedDto {
    private CommentDto commentDto;
    private UserDto commentAuthor;
}
