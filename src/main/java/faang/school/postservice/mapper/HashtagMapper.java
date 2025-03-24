package faang.school.postservice.mapper;

import faang.school.postservice.dto.hashtag.HashtagCreateDto;
import faang.school.postservice.dto.hashtag.HashtagReadDto;
import faang.school.postservice.dto.hashtag.HashtagUpdateDto;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashtagMapper {

    Hashtag toEntity(HashtagCreateDto createDto);

    @Mapping(target = "postIds", source = "posts")
    HashtagReadDto toDto(Hashtag hashtag);

    @IterableMapping(elementTargetType = Long.class)
    default List<Long> mapPostsToIds(List<Post> posts) {
        return Optional.ofNullable(posts)
                .orElse(Collections.emptyList())
                .stream()
                .map(Post::getId)
                .toList();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(HashtagUpdateDto updateDto, @MappingTarget Hashtag hashtag);
}