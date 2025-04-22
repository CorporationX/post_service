package faang.school.postservice.mapper;

import faang.school.postservice.dto.albums.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    AlbumDto toAlbumDto(Album album);

    Album toAlbum(AlbumDto albumDto);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "posts", source = "albumDto.postIds", qualifiedByName = "postIdsToPosts")
    void update(AlbumDto albumDto, @MappingTarget Album album);

    @Named("postIdsToPosts")
    default List<Post> postIdsToPosts(List<Long> postIds) {
        return postIds.stream()
                .map(id -> {
                    Post post = new Post();
                    post.setId(id);
                    return post;
                })
                .collect(Collectors.toList());
    }

    @Named("postsToPostIds")
    default List<Long> postsToPostIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());
    }
}
