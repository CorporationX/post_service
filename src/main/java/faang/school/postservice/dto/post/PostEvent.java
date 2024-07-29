package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEvent {
    @NotNull
    private Long id;

    @NotNull
    private Long authorId;

    @NotNull
    private List<Long> followerIdsAuthor;
}
