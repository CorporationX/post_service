package faang.school.postservice.dto.like;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LikeDto {
    @NotNull
    private long id;
    @NotNull
    private Long userId;
    private ResponsePostDto post;
    private UserDto userDto;
    @NotNull
    private Long postId;
    private CommentDto commentDto;
    @NotNull
    private Long commentId;
}
