package faang.school.postservice.dto.comment;

import faang.school.postservice.model.File;

public record CommentFileReadDto(
        long id,
        long commentId,
        File file
) {
}
