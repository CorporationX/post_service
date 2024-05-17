package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDto {
    private Long id;
    @NotBlank(message = "Post content can't be blank")
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likesIds;
    private List<Long> commentsIds;
    private Boolean published;
    @PastOrPresent(message = "Post can't be published in future")
    private LocalDateTime publishedAt;
    private Boolean deleted;
}
