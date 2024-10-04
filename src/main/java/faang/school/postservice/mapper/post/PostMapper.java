package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.FilterPostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "mapResourcesToResourceIds")
    @Mapping(target = "likes", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    PostResponseDto toDto(Post post);

    Post toEntity(CreatePostRequestDto dto);

    Post toEntity(UpdatePostRequestDto dto);

    Post toEntity(FilterPostRequestDto dto);

    default List<PostResponseDto> listEntitiesToListDto(List<Post> posts) {
        return posts.stream()
                .map(this::toDto)
                .toList();
    }

    @Named("mapResourcesToResourceIds")
    default List<Long> mapResourcesToResourceIds(List<Resource> resources) {
        if (resources == null) {
            return new ArrayList<>();
        }
        return resources.stream()
                .map(Resource::getId)
                .toList();
    }
}