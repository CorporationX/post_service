package faang.school.postservice.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PostViewEvent {
    private Long postId;
    private Long viewerId;
}
