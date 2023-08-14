package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(target = "postsIds", source = "posts", qualifiedByName = "postsToIds")
    AlbumDto toDto(Album album);

    @Mapping(target = "posts", source = "postsIds", qualifiedByName = "idsToPosts")
    Album toEntity(AlbumDto albumDto);

    @Named("postsToIds")
    default List<Long> ids (List<Post> posts) {
      return posts.stream()
                .map(Post::getId)
                .toList();
    }

    @Named("idsToPosts")
    default List<Post> posts (List<Long> ids) {
        return ids.stream()
                .map(id -> Post.builder().id(id).build())
                .toList();
    }
}
