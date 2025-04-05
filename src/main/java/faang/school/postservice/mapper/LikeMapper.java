package faang.school.postservice.mapper;

import faang.school.postservice.model.Identifiable;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.event.LikeEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LikeMapper {

    @Mapping(target = "postId", source = "post.id")
    LikeEvent toEvent(Like postEvent);

    default Long toId(Identifiable identifiable) {
        if (identifiable == null) {
            return null;
        }
        return identifiable.getId();
    }
}