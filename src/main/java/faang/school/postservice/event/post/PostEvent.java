package faang.school.postservice.event.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEvent {
    private long id;
    private long authorId;
    private String content;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}