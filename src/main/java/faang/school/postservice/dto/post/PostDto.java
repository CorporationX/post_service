package faang.school.postservice.dto.post;

import faang.school.postservice.model.PostStatus;
import jakarta.annotation.Nullable;

public record PostDto(
     String content,
     Long authorId,
     @Nullable
     Long projectId,
     PostStatus postStatus
) {
}
