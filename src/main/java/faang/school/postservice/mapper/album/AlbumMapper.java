package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(source = "allowedUsersIds", target = "allowedUsersIds", ignore = true)
    AlbumDto toDto(Album album);

    @Mapping(source = "allowedUsersIds", target = "allowedUsersIds", ignore = true)
    Album toEntity(AlbumDto albumDto);
}
