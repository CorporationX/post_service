package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;


public record PostDto(
        Long id,
        @NotBlank(message = "Содержимое поста не может быть пустым")
        String content,
        Long authorId,
        Long projectId,
        List<String> resourceKeys,
        LocalDateTime scheduledAt
) {

    public PostDto(Long id, String content, Long authorId, Long projectId) {
        this(id, content, authorId, projectId, List.of());
    }

    public PostDto(Long id, String content, Long authorId, Long projectId, List<String> resourceKeys) {
        this(id, content, authorId, projectId, resourceKeys, null);
    }
}
