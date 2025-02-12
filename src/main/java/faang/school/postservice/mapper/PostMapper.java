package faang.school.postservice.mapper;

import faang.school.postservice.dto.posts.PostDto;
import faang.school.postservice.dto.posts.PostSaveDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toEntity(PostSaveDto postSaveDto);

    @Mapping(source = "publishedAt", target = "publishedDate")
    @Mapping(source = "likes", target = "likeCount", qualifiedByName = "count")
    PostDto toDto(Post post);

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "projectId", ignore = true)
    void update(@MappingTarget Post post, PostSaveDto postSaveDto);

    List<PostDto> toDto(List<Post> posts);

    @Named("count")
    default int count(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }
}
