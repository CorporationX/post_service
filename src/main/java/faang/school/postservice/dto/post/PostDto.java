package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
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

    private Long id;

    @NotBlank(message = "Post content can't be blank")
    private String content;

    private Long authorId;

    private Long projectId;

    private List<Long> likesIds;

    private List<Long> commentsIds;

    private boolean published;

    @PastOrPresent(message = "Post can't be published in future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;

    private boolean deleted;
}
