package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "ad.id", source = "adId")
    @Mapping(target = "likes", expression = "java(new ArrayList<>())")
    @Mapping(target = "comments", expression = "java(new ArrayList<>())")
    @Mapping(target = "albums", expression = "java(new ArrayList<>())")
    @Mapping(target = "published", expression = "java(false)")
    @Mapping(target = "deleted", expression = "java(false)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Post toEntity(PostDto dto);

    @Mapping(target = "adId", source = "ad.id")
    PostDto toDto(Post entity);

    List<PostDto> toDtos(List<Post> entities);
}
