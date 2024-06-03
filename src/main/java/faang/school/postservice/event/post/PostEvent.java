package faang.school.postservice.event.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEvent {
    private Long id;
    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedAt;
}
