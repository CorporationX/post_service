package faang.school.postservice.dto.redis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.postservice.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostInRedisDto {
    private long id;
    private String content;
    private Long authorId;
    private List<Comment> comments;
}
