package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Post comment DTO")
public record CommentDto(
        @Schema(description = "Unique identifier of the comment")
        Long id,

        @Schema(description = "Content of the comment")
        String content,

        @Schema(description = "ID of the comment's author")
        Long authorId,

        @Schema(description = "ID of the post associated with the comment")
        Long postId,

        @Schema(description = "Date and time the comment was created")
        LocalDateTime createdAt
) {
}
