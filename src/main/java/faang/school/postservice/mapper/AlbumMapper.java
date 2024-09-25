package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.post.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(source = "posts", target = "postIds", qualifiedByName = "postsToPostIdsMapper")
    AlbumDto toDto(Album album);

    @Mapping(target = "posts", ignore = true)
    Album toEntity(AlbumDto albumDto);

    @Named("postsToPostIdsMapper")
    default List<Long> postsMapper(List<Post> posts) {
        return posts == null ? new ArrayList<>() : posts.stream().mapToLong(Post::getId).boxed().toList();
    }
}