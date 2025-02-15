package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "hashtags", expression = "java(toHashtags(post.getHashtags()))")
    @Mapping(target = "filesKeys", expression = "java(toFileKeys(post.getResources()))")
    ResponsePostDto toDto(Post post);

    @Mapping(target = "hashtags", ignore = true)
    Post toEntity(CreatePostDto createPostDto);

    @Named("toHashtags")
    default Set<String> toHashtags(Set<Hashtag> hashtags) {
        return Optional.ofNullable(hashtags)
                .orElse(Set.of())
                .stream()
                .map(Hashtag::getTag)
                .collect(Collectors.toSet());
    }

    @Named("toFileKeys")
    default List<String> toFileKeys(List<Resource> resources) {
        return Optional.ofNullable(resources)
                .orElse(List.of())
                .stream()
                .map(Resource::getKey)
                .toList();
    }
}
