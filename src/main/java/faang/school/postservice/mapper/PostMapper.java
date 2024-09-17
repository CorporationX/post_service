package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikesToLikeIds")
    PostDto toDto(Post post);

    @Mapping(target = "likes", ignore = true)
    Post toEntity(PostDto postDto);

    List<PostDto> toDtoList(List<Post> posts);

    @Named("mapLikesToLikeIds")
    default List<Long> mapLikesToLikeIds(List<Like> likes){
        return likes.stream()
                .map(Like::getId)
                .toList();
    }
}
