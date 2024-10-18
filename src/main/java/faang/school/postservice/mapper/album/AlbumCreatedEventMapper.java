package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumCreatedEvent;
import faang.school.postservice.mapper.DateTimeMapper;
import faang.school.postservice.protobuf.generate.AlbumCreatedEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumCreatedEventMapper extends DateTimeMapper {

    AlbumCreatedEventProto.AlbumCreatedEvent toFollowerEvent(AlbumCreatedEvent event);
}
