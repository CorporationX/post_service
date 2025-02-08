package faang.school.postservice.mapper;

import faang.school.postservice.dto.posts.PostDto;
import faang.school.postservice.dto.posts.PostSaveDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toEntity(PostSaveDto postSaveDto);

    @Mapping(source = "publishedAt", target = "publishedDate")
    PostDto toDto(Post post);

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "projectId", ignore = true)
    void update(@MappingTarget Post post, PostSaveDto postSaveDto);

    List<PostDto> toDto(List<Post> posts);
}
