package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumAuthorDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AlbumMapper {
    AlbumDto toDto(Album album);
    Album toAlbum(AlbumDto albumDto);
    AlbumAuthorDto toAuthorDto(Album album);
}
