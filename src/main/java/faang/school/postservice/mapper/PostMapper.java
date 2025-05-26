package faang.school.postservice.mapper;

import faang.school.postservice.dto.CreatePostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    CreatePostDto toCreatedPostDto(Post post);

    Post toEntity(CreatePostDto dto);

    @Mapping(target = "amountLikes",
            expression = "java(post.getLikes() != null && !post.getLikes().isEmpty() ? post.getLikes().size() : 0)")
    PostRedis toPostRedis(Post post);
}
