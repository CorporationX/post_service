package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "posts")
public class PostDto implements Serializable {
    private Long id;

    private Boolean published;
    private Boolean deleted;

    private Long authorId;
    private Long projectId;

    @NotBlank(message = "Post content is required")
    @Length(max = 4096, message = "Post content can not be longer than 4096 characters")
    private String content;

    private Long likesCount;

    private List<CommentDto> comments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}
