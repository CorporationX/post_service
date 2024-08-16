package faang.school.postservice.dto.like;

import faang.school.postservice.controller.LikeToComment;
import faang.school.postservice.controller.LikeToPost;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

    @NotNull(message = "Id must be provided", groups = {LikeToPost.class, LikeToComment.class})
    private Long id;

    @Min(value = 0, message = "User ID must be positive", groups = {LikeToPost.class, LikeToComment.class})
    @NotNull(message = "User ID must be provided", groups = {LikeToPost.class, LikeToComment.class})
    private long userId;

    @Positive(message = "Comment id not specified or negative", groups = LikeToComment.class)
    private long commentId;

    @Positive(message = "Post id not specified or negative", groups = LikeToPost.class)
    private long postId;
}