package faang.school.postservice.dto.kafka.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
public record PostEventDto(
    long authorId,
    long postId,
    List<Long> followerIds
){

}
