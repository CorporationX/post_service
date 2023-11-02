package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private long id;
    @NotEmpty(message = "Comment can't be empty")
    @Size(max = 4096, message = "The length of comment can't be more then 4096 symbols")
    private String content;
    @NotNull(message = "Comment can't be without author")
    private long authorId;
    private List<LikeDto> likes;
    @NotNull(message = "Comment can't be write to unknown post")
    private long postId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
