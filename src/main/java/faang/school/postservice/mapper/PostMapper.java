package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(source = "likes", target = "likes", qualifiedByName = "getLikes")
    PostDto toDto(Post post);
    @Mapping(target = "likes", ignore = true)
    Post toEntity(PostDto postDto);

    @Named("getLikes")
    default List<Long> getLikes(List<Like> likes) {
        return likes.stream().map(Like::getId).toList();
    }
}
