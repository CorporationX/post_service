package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime cratedAt;
    private LocalDateTime publishedAt;
    private boolean published;
    private boolean deleted;
}
