package faang.school.postservice.dto.redisCache;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedCache {

    @Id
    private Long userId;
    private Set<Long> feedIds;
}
