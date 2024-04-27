package faang.school.postservice.dto.event;

import faang.school.postservice.dto.LikeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeEventKafka {
    private long postId;
    private long likerId;
    private LikeDto likeDto;
}
