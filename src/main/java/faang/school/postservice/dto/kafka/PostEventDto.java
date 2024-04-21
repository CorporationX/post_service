package faang.school.postservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEventDto {
    private Long postId;
    private Long authorId;
    private LocalDateTime publishedAt;
    private Set<Long> authorSubscriberIds = new HashSet<>();
}