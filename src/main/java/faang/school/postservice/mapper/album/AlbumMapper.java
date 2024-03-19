package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(source = "posts", target = "postsIds", qualifiedByName = "postsToIds")
    AlbumDto toDto(Album album);

    Album toEntity(AlbumDto albumDto);

    List<AlbumDto> toDto(List<Album> albums);

    @Named("postsToIds")
    default List<Long> postsToIds(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return Collections.emptyList();
        }
        return posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());
    }
}
