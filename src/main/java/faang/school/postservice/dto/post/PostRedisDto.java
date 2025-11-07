package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Indexed;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Indexed
public class PostRedisDto {

    private String content;

    private Long authorId;

    private Long projectId;

    private Long id;

    private boolean deleted;

    private boolean published;

    private LocalDateTime publishedAt;
}
