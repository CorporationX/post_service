package faang.school.postservice.dto.kafka;

// import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KafkaPostMessage {
    private Long id;
    private String content;
    // private Long totalLikes;
    // private Long totalComments;
    // private Long authorId;
    // private LocalDateTime publishedAt;
}
