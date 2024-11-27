package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.protobuf.generate.PostViewEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostViewEventMapper extends DateTimeMapper {
    PostViewEventProto.PostViewEvent toProto(PostViewEvent postViewEvent);

    PostViewEvent toEvent(PostViewEventProto.PostViewEvent proto);
}
