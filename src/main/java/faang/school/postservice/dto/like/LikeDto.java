package faang.school.postservice.dto.like;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LikeDto {
    @NotNull
    private long id;
    @NotNull
    private Long userId;
    private Comment comment;
    private ResponsePostDto post;
    private UserDto userDto;
    @NotNull
    private Long postId;
}
