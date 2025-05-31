package faang.school.postservice.mapper;

import faang.school.postservice.dto.CreatePostDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "amountLikes",
                        expression = "java(post.getLikes() != null && !post.getLikes().isEmpty() ? post.getLikes().size() : 0)")
                @Mapping(source = "comments", target="commentIds")
    @Mapping(source = "albums", target = "albumIds")
    PostDto toDto(Post post);

    CreatePostDto toCreatedPostDto(Post post);

    Post toEntity(CreatePostDto dto);

    @Mapping(target = "amountLikes",
            expression = "java(post.getLikes() != null && !post.getLikes().isEmpty() ? post.getLikes().size() : 0)")
    PostRedis toPostRedis(Post post);
}
