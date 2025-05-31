package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostViewEvent {
    private Long postId;
    private Long userId;
    private LocalDateTime viewedAt;
}