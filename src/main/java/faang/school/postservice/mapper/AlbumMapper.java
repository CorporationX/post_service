package faang.school.postservice.mapper;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;


@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(source = "posts", target = "postIds", qualifiedByName = "map")
    AlbumDto toDto(Album event);

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "id", ignore = true)
    void update(AlbumDto eventDto, @MappingTarget Album entity);

    @Named("map")
    default List<Long> map(List<Post> posts) {
        if (posts == null) {
            return new ArrayList<>();
        }
        return posts.stream().map(Post::getId).toList();
    }
}
