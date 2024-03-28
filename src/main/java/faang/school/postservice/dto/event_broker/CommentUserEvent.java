package faang.school.postservice.dto.event_broker;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentUserEvent {
    private Long postId;
    private String content;
    private Long authorId;
    private UserDto userDto;
}
