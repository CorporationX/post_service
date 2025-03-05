package faang.school.postservice.kafka.event;

import faang.school.postservice.model.LikeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewLikeEvent implements Serializable {
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;
    private LikeType type;
}