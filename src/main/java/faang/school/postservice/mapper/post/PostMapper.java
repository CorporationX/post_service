package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.dto.post.DraftPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.PreviewPostResourceDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "scheduledAt", target = "scheduledAt")
    PostDto fromDraftPostDto(DraftPostDto draftPostDto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(source = "content", target = "content")
    @Mapping(target = "likesCount", source = "likes", qualifiedByName = "mapLikes")
    @Mapping(source = "views", target = "views")
    CachedPostDto toCachedPostDto(Post post);

    List<CachedPostDto> toCachedPostDtoList(List<Post> posts);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "scheduledAt", target = "scheduledAt")
    @Mapping(source = "publishedAt", target = "publishedAt")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "resources", target = "resources", qualifiedByName = "mapResources")
    @Mapping(target = "likesCount", source = "likes", qualifiedByName = "mapLikes")
    @Mapping(target = "commentsCount", source = "comments", qualifiedByName = "mapComments")
    PostDto toDto(Post post);

    @Named("mapResources")
    default List<PreviewPostResourceDto> mapResources(List<Resource> resources) {

        if (resources == null) {
            return Collections.emptyList();
        }

        return resources.stream()
                .map(
                        r -> new PreviewPostResourceDto(
                                r.getId(),
                                r.getName()
                        )
                )
                .toList();
    }

    @Named("mapLikes")
    default long mapLikes(List<Like> likes) {
        if (likes == null) {
            return 0;
        }
        return likes.size();
    }

    @Named("mapComments")
    default long mapComments(List<Comment> comments) {
        if (comments == null) {
            return 0;
        }
        return comments.size();
    }


    @Mapping(source = "postDto.id", target = "id")
    @Mapping(source = "postDto.authorId", target = "authorId")
    @Mapping(source = "postDto.projectId", target = "projectId")
    @Mapping(source = "postDto.content", target = "content")
    @Mapping(source = "postDto.scheduledAt", target = "scheduledAt")
    @Mapping(source = "postDto.publishedAt", target = "publishedAt")
    @Mapping(source = "postDto.createdAt", target = "createdAt")
    @Mapping(source = "postDto.updatedAt", target = "updatedAt")
    @Mapping(source = "postDto.resources", target = "resources", qualifiedByName = "mapPreviewPostResourceDto")
    Post toEntity(PostDto postDto);

    @Named("mapPreviewPostResourceDto")
    default List<Resource> mapPreviewPostResourceDto(List<PreviewPostResourceDto> resourceDtos) {
        if (resourceDtos == null) {
            return null;
        }
        return resourceDtos.stream()
                .map(resDto -> Resource.builder().id(resDto.getId()).build())
                .toList();
    }
}
