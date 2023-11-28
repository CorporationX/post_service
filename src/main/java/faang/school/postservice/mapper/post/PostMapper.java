package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.redis.PostAchievementEventDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.FIELD)
public interface PostMapper {

    @Mapping(source = "likes", target = "likes", qualifiedByName = "getLikes")
    PostDto toDto(Post post);

    List<PostDto> toDtoList(List<Post> posts);

    @Mapping(target = "likes", ignore = true)
    Post toEntity(PostDto postDto);

    @Mapping(target = "postId", source = "id")
    PostAchievementEventDto toEventDto(PostDto postDto);

    @Named("getLikes")
    default List<Long> getLikes(List<Like> likes) {
        return likes.stream().map(Like::getId).toList();
    }
}
