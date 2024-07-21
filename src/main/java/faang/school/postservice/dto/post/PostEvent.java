package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostEvent {
    @NotNull
    private Long id;

    @NotNull
    private Long authorId;

    @NotNull
    private List<Long> followersAuthor;
}
