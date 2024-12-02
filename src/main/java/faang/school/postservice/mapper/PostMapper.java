package faang.school.postservice.mapper;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "scheduledAt", ignore = true)
    Post toPost(PostDto postDto);
    PostDto toPostDto(Post post);
    List<PostDto> toPostDtos(List<Post> posts);
}