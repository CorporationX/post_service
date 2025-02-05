package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.utils.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toEntity(PostCreateDto dto);

    @Mapping(target = "likesCount",
            expression = "java(entity.getLikes().size())")
    PostReadDto toDto(Post entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "content", conditionQualifiedByName = "isNotBlank")
    void updateEntityFromDto(PostUpdateDto dto, @MappingTarget Post entity);

    @Condition
    @Named("isNotBlank")
    default boolean isNotBlank(String value) {
        return StringUtils.isNotBlank(value);
    }
}