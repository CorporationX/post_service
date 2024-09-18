package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(source = "posts", target = "postIds", qualifiedByName = "mapToLongList")
    AlbumDto toDto(Album album);

    @Mapping(source = "postIds", target = "posts", qualifiedByName = "mapToPostList")
    Album toEntity(AlbumDto albumDto);

    List<AlbumDto> toDtoList(List<Album> albums);

    @Named("mapToPostList")
    default List<Post> mapToPostList(List<Long> postIds) {
        if (Objects.isNull(postIds)) {
            postIds = List.of();
        }

        return postIds.stream()
                .map(postId -> Post.builder()
                        .id(postId)
                        .build())
                .toList();
    }

    @Named("mapToLongList")
    default List<Long> mapToLongList(List<Post> posts){
        return posts.stream()
                .map(Post::getId)
                .toList();
    }
}
