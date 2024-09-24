package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.comment.validation.group.Create;
import faang.school.postservice.dto.comment.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;

public record CommentCreateUpdateDto(

        @NotBlank(groups = {Create.class, Update.class}, message = "Content for comment is required, and cannot be empty or blank")
        String content,

        @NotNull(groups = {Create.class}, message = "Author id is required")
        @Positive(groups = {Create.class}, message = "Author id must be positive")
        @Null(groups = {Update.class}, message = "Author id cannot be updated")
        Long authorId
) {
}
