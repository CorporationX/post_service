package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentEventDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private long id;
    @NotBlank
    private String content;
    @Min(value = 0)
    @NotNull
    private Long authorId;
    @Min(value = 0)
    @NotNull
    private Long projectId;
    boolean published;
    private Integer likes;
    private Integer views;
    private List<CommentEventDto> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
