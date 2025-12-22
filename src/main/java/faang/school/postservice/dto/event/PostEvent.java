package faang.school.postservice.dto.event;

import java.util.List;

public record PostEvent(
		Long postId,
		Long authorId,
		String content,
		List<Long> subscriberIds
) {
}