package faang.school.postservice.mapper;

import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toPost(PostDto postDto);

    PostDto toPostDto(Post post);

    List<PostDto> toPostsDto(List<Post> posts);
}
