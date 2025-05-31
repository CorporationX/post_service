package faang.school.postservice.dto.post;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostFeedEvent {
    private long postId;
    private long authorId;
    private LocalDateTime publishedAt;
    private List<UserDto> subscribersIds;
}