package faang.school.postservice.dto.comment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    @NotBlank(message = "Content should not be blank")
    @Length(max = 4096, message = "The length of the comment cannot exceed 4096 characters")
    private String content;
    private Long authorId;
    private List<Long> likeIds;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
