package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

        private Long id;
        private Long authorId;
        private UserDto author;

        @NotBlank(message = "The content is empty")
        @Size(min = 1, max = 4096, message = "The content size should be between 1 and 4096 characters")
        private String content;

        @NotNull(message = "Likes list cannot be null")
        private List<LikeDto> likes;

        private List<CommentDto> comments;
        private Long viewsCount;
        private Long projectId;
        private boolean published;
        private boolean deleted;
        private LocalDateTime scheduledAt;
}
