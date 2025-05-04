package faang.school.postservice.dto.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEventDto implements Serializable {
    private long postId;
    private LocalDateTime postCreatedAt;
    private List<Long> postAuthorFollowersIds;
}
