package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.comment.validation.group.Create;
import faang.school.postservice.dto.comment.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentDto(

        @Null(groups = {Create.class, Update.class}, message = "Id not required")
        Long id,

        @NotBlank(groups = {Create.class, Update.class}, message = "Content for comment is required, and cannot be empty or blank")
        @Size(max = 4096, groups = {Create.class, Update.class}, message = "Content length must not be more than 4096 symbols")
        String content,

        @NotNull(groups = {Create.class}, message = "Author id is required")
        @Positive(groups = {Create.class}, message = "Author id must be positive")
        @Null(groups = {Update.class}, message = "Author id cannot be updated")
        Long authorId,

        @Null(groups = {Create.class, Update.class}, message = "Updated time not required")
        LocalDateTime updatedAt) {
}
