package faang.school.postservice.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PostEvent {
    private long authorId;
    private long postId;
}
