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

    @NotNull
    private Long id;

    @NotBlank
    private String content;

    @NotNull
    private Long authorId;

    @NotNull
    private Long postId;

    @Min(0)
    private int likesCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String largeImageFileKey;
    private String smallImageFileKey;
}


