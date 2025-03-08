package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDTO;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    @Mapping(source = "posts", target = "postIds", qualifiedByName = "mapPostToId")
    AlbumDTO toDto(Album album);

    Album toEntity(AlbumDTO dto);

    @Named("mapPostToId")
    default List<Long> mapPostToId(List<Post> posts) {
        if(posts != null) {
            return posts.stream().map(Post::getId).toList();
        }
        return null;
    }

    List<AlbumDTO> toDtoList(List<Album> albums);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "posts", ignore = true)
    void update(AlbumDTO albumDTO, @MappingTarget Album album);
}
