package faang.school.postservice.kafka.events;

import lombok.Builder;

import java.util.List;

@Builder
public record PostFollowersEvent(
        Long authorId,
        Long postId,
        List<Long> followersIds //TODO Int totalFollower
) {}