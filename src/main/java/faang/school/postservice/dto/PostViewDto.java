package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Alexander Bulgakov
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostViewDto {
    private long id;
    private long viewerId;
    private long postId;
    private LocalDateTime viewedAt;
}
