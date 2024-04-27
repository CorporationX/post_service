package faang.school.postservice.mapper;

import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.dto.post.PostDto;
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

    @Mapping(target = "likeCount", source = "likes", qualifiedByName = "toLikeCount")
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    @Named("toLikeCount")
    default Long toLikeCount(List<Like> likes) {
        return likes != null ? likes.size() : 0L;
    }
}
