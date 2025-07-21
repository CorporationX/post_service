package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LikeDto {
    private long id;
    private long userId;
}
