package faang.school.postservice.kafka.events;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostCreatedEvent {
    List<UserDto> followers;
    private Long postId;
    private Long authorId;
}
