package faang.school.postservice.dto.feed;

import lombok.Builder;

import java.util.List;

@Builder
public record FeedHeaterEvent(List<Long> userIds) {
}
