package faang.school.postservice.dto.comment;

import faang.school.postservice.model.CommentDtoStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentForUpdateDto extends CommentDto{
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Content cannot be null")
    @Size(min = 1, max = 4096, message = "Content cannot be empty")
    private String content;

    private CommentDtoStatus status;
}
