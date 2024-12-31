package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;

    @Size(min = 1, max = 4096)
    @NotBlank(message = "Content cannot be blank")
    @NotNull(message = "Content cannot be null")
    private String content;

    @NotNull(message = "AuthorID cannot be null")
    @Positive(message = "AuthorID must be positive")
    private Long authorId;

    @NotNull(message = "PostID cannot be null")
    @Positive(message = "PostID must be positive")
    private Long postId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


    @JsonCreator
    public CommentDto(
            @JsonProperty("id") Long id,
            @JsonProperty("content") String content,
            @JsonProperty("authorId") Long authorId,
            @JsonProperty("postId") Long postId,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("updatedAt") LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.postId = postId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
