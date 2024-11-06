package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.model.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {

    PostCache toPostCache(PostDto postDto);

    PostDto toDto(PostCache postCache);

    List<PostDto> toDto(List<PostCache> postCache);

    @Mapping(source = "likes", target = "likes", qualifiedByName = "countTotalLikes")
    PostCache fromEntitytoPostCache(Post post);

    @Named("countTotalLikes")
    default Integer countTotalLikes(List<Like> likes){
        return likes == null ? 0 : likes.size();
    }
}
