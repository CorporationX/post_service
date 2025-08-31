package faang.school.postservice.dto.event;

import lombok.Data;

@Data
public class LikeEvent {
    private String postId;
    private String userId;
    private long timestamp;
}