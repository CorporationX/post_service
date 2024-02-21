package faang.school.postservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePostEvent {
    private Long postId;
    private Long counterLikes;
    private Long counterComments;
    private List<Long> followersIds;
}
