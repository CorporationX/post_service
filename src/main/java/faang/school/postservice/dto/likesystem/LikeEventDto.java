package faang.school.postservice.dto.likesystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeEventDto {
    private long actorId;
    private long receiverId;
    private LocalDateTime receivedAt;
}

