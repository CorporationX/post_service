package faang.school.postservice.mapper;

import faang.school.postservice.model.Post;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.post.PostDto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toModel(CreatePostDto dto);

    PostDto toDto(Post post);

    void updateModel(UpdatePostDto updatePostDto, @MappingTarget Post post);
}
