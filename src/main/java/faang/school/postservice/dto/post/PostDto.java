package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDto {
    private Long id;

    @NotBlank(message = "Post content could not be blank")
    private String content;

    @NotNull(message = "Author id could not be null")
    private Long authorId;

    private Long projectId;

    private List<Long> likeIds;

    private List<Long> commentIds;

    private List<Long> albumIds;

    private Long adId;

    private List<Long> resourceIds;

    @Null(message = "Published field will be set automatically")
    private Boolean published;

    @Null(message = "Published at field will be set automatically")
    private LocalDateTime publishedAt;

    @Null(message = "Published at field will be set automatically")
    private LocalDateTime scheduledAt;

    @Null(message = "Deleted field will be set automatically")
    private Boolean deleted;

    @Null(message = "CreatedAt field will be set automatically")
    private LocalDateTime createdAt;

    @Null(message = "UpdatedAt field will be set automatically")
    private LocalDateTime updatedAt;
}