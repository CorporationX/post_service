package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentDto {
    @NotNull
    private long id;
    @NotBlank
    private String content;
    @NotNull
    private long authorId;
    @NotEmpty
    private List<LikeDto> likes;
    private ResponsePostDto postDto;
}
