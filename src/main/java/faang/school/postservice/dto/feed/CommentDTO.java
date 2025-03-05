package faang.school.postservice.dto.feed;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDTO {
    private Long id;
    private AuthorDTO author;
    private String content;
    private LocalDateTime createdAt;
}