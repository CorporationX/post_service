package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    @NotNull(message = "id should not be null")
    private Long id;

    @NotBlank(message = "content should not be blank")
    private String content;

    @NotNull(message = "authorId should not be null")
    private Long authorId;

    @NotNull(message = "postId should not be null")
    private Long postId;

    @Min(value = 0, message = "likesCount should be grated than 0")
    @NotNull(message = "authorId should not be null")
    private int likesCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String largeImageFileKey;
    private String smallImageFileKey;
}