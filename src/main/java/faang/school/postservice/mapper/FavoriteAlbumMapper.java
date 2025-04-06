package faang.school.postservice.mapper;

import faang.school.postservice.dto.FavoriteAlbumDto;
import faang.school.postservice.model.FavoriteAlbum;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FavoriteAlbumMapper {
    FavoriteAlbumDto toFavoriteAlbumDto(FavoriteAlbum entity);

    FavoriteAlbum toFavoriteAlbum(FavoriteAlbumDto dto);
}
