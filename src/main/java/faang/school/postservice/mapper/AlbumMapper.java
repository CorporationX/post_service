package faang.school.postservice.mapper;

import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    AlbumDto toAlbumDto(Album album);

    Album toAlbum(AlbumDto albumDto);

   // void update(AlbumDto albumDto, Album album);
}
