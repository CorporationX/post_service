package faang.school.postservice.dto.event;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostEvent {
    private Long id;
    private Long authorId;
    private List<Long> subscriberIds;
}