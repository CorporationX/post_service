package faang.school.postservice.mapper;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.dto.resource.ResourceResponseDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
  
    ResourceResponseDto toResourceDto(Resource resource);

    @Mapping(target = "postId", expression = "java(getPostId(resource))")
    ResourceDto toDto(Resource resource);

    @Mapping(source = "resourceDto.id", target = "id")
    @Mapping(source = "resourceDto.createdAt", target = "createdAt")
    Resource toEntity(ResourceDto resourceDto, Post post);

    default Long getPostId(Resource resource) {
       return resource.getPost().getId();
    }
}
