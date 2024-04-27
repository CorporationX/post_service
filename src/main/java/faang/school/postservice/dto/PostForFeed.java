package faang.school.postservice.dto;

import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedDeque;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostForFeed {

    private Long id;
    @NotBlank(message = "Content is required")
    private String content;
    private Long authorId;
    private UserDto userDto;
    private Long projectId;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private ConcurrentLinkedDeque<CommentDto> comments;
    private ConcurrentLinkedDeque<LikeDto> likes;
    private ConcurrentLinkedDeque<UserDto> views;
}
