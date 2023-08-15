package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private long id;
    @NotNull(message = "Post content must be specified")
    @NotBlank(message = "Post content cannot be empty")
    @Size(max = 4096, message = "Post content must contains less then 4096 symbols")
    private String content;
    @NotNull(message = "There is not author of the post")
    private Long authorId;
    @NotNull(message = "There is not project of the post")
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private boolean published;
    private boolean deleted;
    private List<LikeDto> likes;
}
