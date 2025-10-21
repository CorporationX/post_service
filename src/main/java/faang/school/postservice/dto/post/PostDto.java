package faang.school.postservice.dto.post;

import faang.school.postservice.model.PostStatus;

public record PostDto(
     String content,
     Long authorId,
     PostStatus postStatus
) {
}
