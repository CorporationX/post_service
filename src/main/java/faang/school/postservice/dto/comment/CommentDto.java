package faang.school.postservice.dto.comment;


import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.like.LikeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CommentDto {
    private Long id;

    @NotBlank(message = "Comment content cant be empty")
    @Size(max = 4500, message = "Post content must contains less then 4500 symbols")
    private String content;

    @NotNull(message = "Author cant be null")
    private long authorId;

    private List<LikeDto> likes;

    @NotNull(message = "Post cant be null")
    private Long postId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
