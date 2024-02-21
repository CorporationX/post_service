package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.client.CommentDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private long id;
    @NotBlank(message = "Post content cannot be empty")
    @Size(max = 4096, message = "Post content must contains less then 4096 symbols")
    private String content;
    private Long authorId;
    private Long projectId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;
    private boolean published;
    private boolean deleted;
    private List<LikeDto> likes;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledAt;
}
