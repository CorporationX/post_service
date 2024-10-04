package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.CreateAlbumDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.album.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface AlbumMapper {
    @Mapping(source = "posts", target = "postIds", qualifiedByName = "mapPostsToPostIds")
    AlbumResponseDto toAlbumResponseDto(Album album);

    List<AlbumResponseDto> toAlbumResponseDtos(List<Album> album);

    @Mapping(target = "posts", ignore = true)
    Album toEntity(CreateAlbumDto albumDto);

    @Named("mapPostsToPostIds")
    default List<Long> mapPostsToPostIds(List<Post> posts) {
        if (posts == null) {
            return new ArrayList<>();
        }
        return posts.stream()
                .map(Post::getId)
                .toList();
    }
}
