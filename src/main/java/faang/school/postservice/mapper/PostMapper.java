package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CreatePostRequest;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PostMapper {

    @Mapping(target = "likes", expression = "java(post.getLikes() != null ? (long)post.getLikes().size() : 0)")
    @Mapping(target = "scheduleAt", ignore = true)
    PostDto toDto(Post post);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "published", constant = "false")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "scheduledAt", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "verifiedAt", ignore = true)
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "content", source = "content")
    Post toEntity(CreatePostRequest request);
}
