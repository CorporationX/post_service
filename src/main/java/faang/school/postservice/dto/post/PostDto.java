package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}
