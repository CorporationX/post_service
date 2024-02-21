package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesToDto")
    PostDto toDto(Post post);
    Post toEntity(PostDto postDto);

    @Named("mapLikesToDto")
    default List<LikeDto> mapLikesToDto(List<Like> likes) {
        LikeMapper likeMapper = new LikeMapperImpl();
        if (likes == null) {
            return null;
        }
        return likes.stream()
                .map(likeMapper::toDto)
                .toList();
    }
}
