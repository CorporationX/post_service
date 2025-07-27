package faang.school.postservice.mapper;

import faang.school.postservice.kafka.event.CommentEvent;
import faang.school.postservice.service.redis.dto.CommentCacheDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentEventMapper {

    CommentCacheDto createDtoFromEvent(CommentEvent event);
}
