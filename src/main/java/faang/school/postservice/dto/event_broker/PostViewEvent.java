package faang.school.postservice.dto.event_broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostViewEvent implements Serializable {
    private Long postId;
    private Long viewerId;
    private LocalDateTime time;
}
