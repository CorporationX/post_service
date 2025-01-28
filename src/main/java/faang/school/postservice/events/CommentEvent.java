package faang.school.postservice.events;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class CommentEvent {
    private Long id;
    private String content;
    private Long authorId;
    private List<Long> likeIds = new CopyOnWriteArrayList<>();
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
