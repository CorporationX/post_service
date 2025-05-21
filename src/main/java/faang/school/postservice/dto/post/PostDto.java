package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDto {
    @Null(message = "Post id will be set automatically in database")
    private Long id;

    @NotBlank(message = "Post content could not be blank")
    private String content;

    private Long authorId;

    private Long projectId;

    @Null(message = "You can only manage likes by specified messages")
    private List<Long> likeIds;

    @Null(message = "You can only manage comments by specified messages")
    private List<Long> commentIds;

    @Null(message = "You can only manage albums by specified messages")
    private List<Long> albumIds;

    @Null(message = "You can only manage ad by specified messages")
    private Long adId;

    @Null(message = "You can only manage resources by specified messages")
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