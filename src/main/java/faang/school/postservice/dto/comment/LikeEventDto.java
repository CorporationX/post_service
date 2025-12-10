package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeEventDto {
    private long id;
    private long postId;
    private long authorId;
}
