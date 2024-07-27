package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedCommentDto {

    private Long id;

    @Size(min = 1, max = 4096)
    private String content;

    private LocalDateTime updatedAt;
}
