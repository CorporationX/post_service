package faang.school.postservice.dto.post;

import faang.school.postservice.dto.like.LikeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class PostDto {
        Long id;
        Long authorId;
        @NotBlank(message = "The content is empty")
        @Size(min = 1, max = 4096, message = "The content size should be between 1 and 4096 characters")
        String content;
        @NotNull(message = "Likes list cannot be null")
        List<LikeDto> likes;
        Long projectId;
        boolean published;
        boolean deleted;
        LocalDateTime scheduledAt;
}
