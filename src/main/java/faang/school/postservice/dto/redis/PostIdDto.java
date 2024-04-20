package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostIdDto implements Comparable<PostIdDto> {
    private long postId;
    private LocalDateTime publishedAt;
    @Override
    public int compareTo(PostIdDto postIdDto) {
        return publishedAt.compareTo(postIdDto.publishedAt);
    }
}
