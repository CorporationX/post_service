package faang.school.postservice.dto.kafka.events;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaPostEvent {
    private UserDto author;
    private Long postId;
    private Long countLikes;
    private Long countComments;
    private Long countViews;
    private LocalDateTime createdAt;
}
