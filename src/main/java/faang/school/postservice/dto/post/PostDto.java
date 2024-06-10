package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "PostId can not be null")
    @Positive(message = "PostId should be positive")
    private Long id;

    @NotBlank(message = "Post content can't be blank")
    private String content;

    @NotNull(message = "AuthorId can not be null")
    @Positive(message = "AuthorId should be positive")
    private Long authorId;

    @NotNull(message = "ProjectId can not be null")
    @Positive(message = "ProjectId should be positive")
    private Long projectId;

    private List<Long> likesIds;

    private List<Long> commentsIds;

    private boolean published;

    @PastOrPresent(message = "Post can't be published in future")
    private LocalDateTime publishedAt;

    private boolean deleted;
}
