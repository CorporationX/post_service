package faang.school.postservice.dto.event;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEventKafka {
    private Long postId;
    private Long authorId;
    private String content;
    private UserDto userDto;
}
