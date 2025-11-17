package faang.school.postservice.event;

import lombok.Builder;

import java.util.List;

@Builder
public record UserBanEvent(
        List<Long> userIds
) {
}