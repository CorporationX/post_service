package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedEvent {

    @NotNull
    private Long postId;

    @NotNull
    private Long authorId;

    private List<Long> subscriberIds;
}

