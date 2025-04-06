package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.PostAlbumDto;
import faang.school.postservice.model.PostAlbum;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostAlbumMapper {
    PostAlbum toPostAlbum(PostAlbumDto dto);

    PostAlbumDto toPostAlbumDto(PostAlbum entity);
}
