package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Indexed
public class PostRedisDto {

    private String content;

    private Long authorId;

    private Long projectId;

    private Long id;

    private boolean deleted;

    private boolean published;

    private LocalDateTime publishedAt;

    private List<CommentDto> comments = new CopyOnWriteArrayList<>();

    private Integer likes;
}
