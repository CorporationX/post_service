package faang.school.postservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import faang.school.postservice.validation.like.comment.CommentLikeAnnotationValidator;
import faang.school.postservice.validation.like.post.PostLikeAnnotationValidator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LikeDto {
    private long id;
    @NotNull
    @PositiveOrZero(message = "User Id can't be negative or null")
    private Long userId;
    @NotNull(message = "Comment Id can't be null", groups = CommentLikeAnnotationValidator.class)
    @PositiveOrZero(message = "Comment Id can't be negative or null", groups = CommentLikeAnnotationValidator.class)
    private Long commentId;
    @NotNull(message = "Post Id can't be null", groups = PostLikeAnnotationValidator.class)
    @PositiveOrZero(message = "Post Id can't be negative or null", groups = PostLikeAnnotationValidator.class)
    private Long postId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
