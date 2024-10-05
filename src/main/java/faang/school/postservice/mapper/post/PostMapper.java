package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ProgressPost;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper{

    Post toEntity(PostDto postDto);

    PostDto toDto(Post post);

    List<PostDto> toDto(List<Post> posts);

    List<Post> toEntity(List<ProgressPost> progressPosts);
}
