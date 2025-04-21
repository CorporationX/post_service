package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(source = "likes", target = "likes", qualifiedByName = "countTotalLikes")
    PostDto toDto(Post post);

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostDto postDto);

    @Named("countTotalLikes")
    default Integer countTotalLikes(List<Like> likes){
        return likes == null ? 0 : likes.size();
    }
}