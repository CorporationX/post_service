package faang.school.postservice.dto;

import java.util.List;

public record PostDto(
        long id,
        String content,
        long authorId,
        long projectId,
        int amountLikes,
        List<Long> commentIds,
        List<Long> albumIds
) {
}
