package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toEntity(PostDto postDto);

    PostDto toDto(Post post);

    @Mapping(target = "countLike", expression = "java(context.getCountLike(post.getId()))")
    PostDto toDto(Post post, @Context PostContextMapper context);

    List<PostDto> toDto(List<Post> posts);
  }
}
