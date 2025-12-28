package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostCreatedEventDto(
        UUID eventId,
        Instant occurredAt,
        long postId,
        Publisher publisher,
        List<Long> followerIds,
        int version
) {
}