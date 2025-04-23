package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private Long id;
    private Long authorId;
    private Long projectId;
    private Long likes;
    private LocalDateTime scheduleAt;
}
