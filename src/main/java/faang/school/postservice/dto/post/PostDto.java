package faang.school.postservice.dto.post;

import faang.school.postservice.model.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Long id;
    private List<Like> likes;
    private LocalDateTime publishedAt;
}
