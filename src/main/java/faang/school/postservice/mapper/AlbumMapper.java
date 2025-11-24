package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.FavoriteAlbumsDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.FavoriteAlbums;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AlbumMapper {

    Album toAlbum(AlbumDto albumDto);

    AlbumDto toDto(Album album);

    Album updateAlbum(AlbumDto albumDto, @MappingTarget Album album);

    List<AlbumDto> toDtoList(List<Album> albums);

    List<Album> toAlbumList(List<AlbumDto> albumDtos);

    FavoriteAlbumsDto toFavoriteAlbumDto(FavoriteAlbums favoriteAlbums);
}
