package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.comment.CommentDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PostDto implements Serializable {

    private Long id;
    @NotBlank(message = "content cannot be null!")
    private String content;
    private Long authorId;
    private Long projectId;
    private Long likeCount;
    private List<CommentDto> comments;
    private boolean published;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
