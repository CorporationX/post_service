package faang.school.postservice.dto.event;

import jakarta.validation.constraints.NotEmpty;
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
public class PostEventDto extends EventDto {
    @NotNull
    private Long postId;

    @NotEmpty
    private List<Long> authorFollowersIds;

    @NotNull
    private Long authorId;
}