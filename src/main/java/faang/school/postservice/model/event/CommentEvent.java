package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent extends Event {
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
}
