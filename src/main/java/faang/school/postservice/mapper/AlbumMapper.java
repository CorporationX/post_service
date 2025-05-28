package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

    @Mapping(target = "albumId", source = "id")
    @Mapping(target = "postsIds", source = "posts", qualifiedByName = "mapPostsToIds")
    AlbumUpdateDto toDto(Album album);

    List<AlbumUpdateDto> toDtoList(List<Album> albums);

    @Named("mapPostsToIds")
    default List<Long> mapPostsToIds(List<Post> posts) {
        if (posts == null) {
            return null;
        }
        return posts.stream()
                .map(Post::getId)
                .toList();
    }
}
