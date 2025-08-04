package faang.school.postservice.dto.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommentImageDto {
    private Long id;
    @NotBlank(message = "content must not be blank")
    private String content;
    @NotNull(message = "author id must not be null")
    private Long authorId;
    @NotNull(message = "post id must not be null.")
    private Long postId;
    private String largeObjectKey;
    private String smallObjectKey;
    private String urlLarge;
    private String urlThumb;
}
