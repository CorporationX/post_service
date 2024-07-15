package faang.school.postservice.dto.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostEventDto extends EventDto {
    @NotNull
    private Long postId;

    @NotEmpty
    private List<Long> authorFollowersIds;

    @NotNull
    private Long authorId;
}
