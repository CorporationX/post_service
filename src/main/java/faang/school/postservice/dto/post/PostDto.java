package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Post content cant be empty")
    @Size(max = 4500, message = "Post content must contains less then 4500 symbols")
    private String content;
    @NotNull(message = "Author cant be null")
    private Long authorId;
    @NotNull(message = "Project cant be null")
    private Long projectId;
    private List<LikeDto> likes;
    private List<CommentDto> comments;
    private boolean published;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;
    private boolean deleted;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
