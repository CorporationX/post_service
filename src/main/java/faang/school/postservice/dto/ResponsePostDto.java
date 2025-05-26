package faang.school.postservice.dto;

import java.util.List;

public record ResponsePostDto(
        long id,
        String content,
        long authorId,
        long projectId,
        int amountLikes,
        List<CommentDto> commentIds
) {
}
