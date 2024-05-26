package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(source = "posts", target = "postIds", qualifiedByName = "toPostIds")
    AlbumDto toDto(Album album);

    Album toEntity(AlbumDto albumDto);

    @Named("toPostIds")
    default List<Long> toPostIds(List<Post> posts) {
        if (Objects.nonNull(posts)) {
            return posts.stream()
                    .map(Post::getId)
                    .toList();
        }
        return new ArrayList<>();
    }
}
