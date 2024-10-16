package faang.school.postservice.mapper;

import faang.school.postservice.model.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(target = "postIds", source = "posts", qualifiedByName = "mapPostsToIds")
    AlbumDto albumToAlbumDto(Album album);

    List<AlbumDto> toDtoList(List<Album> albums);

    @Named("mapPostsToIds")
    default List<Long> mapPostsToIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());
    }
}

