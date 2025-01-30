package faang.school.postservice.dto;

import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;

    @NotNull
    @Size(min = 1, max = 4096, message = "Content must not exceed 4096 characters")
    private String content;

    private Long authorId;
    private UserDto author;

    private Long projectId;
    private List<CommentDto> comments;
    private Long adId;
    private List<Long> resourcesIds;
    private boolean published;
    private LocalDateTime publishedAt;

    @Future(message = "Scheduled date is not in the future.")
    private LocalDateTime scheduledAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
}
