package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto implements Serializable {
    private Long id;

    private Long authorId;
    private Long projectId;

    @NotBlank(message = "Content can't be null or blank")
    @Size(min = 1, max = 4096)
    private String content;

    private List<CommentDto> comments;
//    private List<Long> likes;
}
