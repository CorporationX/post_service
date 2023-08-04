package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface AlbumMapper {
    @Mapping(target = "posts", source = "postsId", qualifiedByName = "toAlbum")
    Album toAlbum(AlbumDto albumDto);

    @Mapping(target = "postsId", source = "posts", qualifiedByName = "toId")
    AlbumDto toAlbumDto(Album album);

    @Named("toId")
    default List<Long> toId(List<Album> albums){
        if (albums == null) {
            return new ArrayList<>();
        }
        return albums.stream().map(Album::getId).toList();
    }

    @Named("toAlbum")
    default List<Album> toAlbum(List<Long> ids){
        if (ids == null) {
            return new ArrayList<>();
        }
        List<Album> albums = new ArrayList<>();
        for (Long id : ids) {
            albums.add(Album.builder().id(id).build());
        }
        return albums;
    }
}
