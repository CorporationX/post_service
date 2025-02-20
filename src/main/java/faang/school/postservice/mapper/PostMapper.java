package faang.school.postservice.mapper;

import faang.school.postservice.dto.Post.CreatePostDraftDto;
import faang.school.postservice.dto.Post.PostResponseDto;
import faang.school.postservice.dto.Post.UpdatePostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "adId", source = "ad.id")
    @Mapping(target = "likesIds",
            expression = "java(mapEntitiesToIds(post.getLikes(), like -> like.getId()))")
    @Mapping(target = "commentsIds",
            expression = "java(mapEntitiesToIds(post.getComments(), comment -> comment.getId()))")
    @Mapping(target = "likesCount",
            expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    PostResponseDto toResponseDto(Post post);

    Post update(@MappingTarget Post post, UpdatePostDto postDto);

    Post fromCreateDto(CreatePostDraftDto postDto);

    default <T> List<Long> mapEntitiesToIds(List<T> entities, Function<T, Long> toId) {
        return entities == null ? new ArrayList<>() :
                entities.stream()
                        .map(toId)
                        .collect(Collectors.toCollection(ArrayList::new));
    }
}