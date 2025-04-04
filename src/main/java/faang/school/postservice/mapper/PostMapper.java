package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = CommentMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    @Mapping(source = "likes", target = "likes", qualifiedByName = "countTotalLikes")
    PostDto toDto(Post post);

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostDto postDto);

    List<PostDto> toDto(List<Post> posts);

    List<Post> toEntity(List<PostDto> postDtos);

    @Named("countTotalLikes")
    default Long countTotalLikes(List<Like> likes) {
        return likes == null ? 0L : (long) likes.size();
    }
}