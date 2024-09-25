package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    Album toEntity(AlbumDto albumDto);

    AlbumDto toDto(Album album);

    List<AlbumDto> toDto(List<Album> albums);

    void updateFromDto(AlbumDto albumDto, @MappingTarget Album albumToUpdate);
}
