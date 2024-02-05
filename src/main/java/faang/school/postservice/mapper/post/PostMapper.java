package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likes", ignore = true)
    Post toEntity(PostDto postDto);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "toLikeIds")
    PostDto toDto(Post post);


    @Named("toLikeIds")
    default List<Long> toLikeIds(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream().map(Like::getId).toList();
    }
}
