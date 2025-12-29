package faang.school.postservice.mapper;

import faang.school.postservice.cache.model.post.PostCache;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {

    PostDto toPostDto(Post post);

    Post toPost(CreatePostDto createPostDto);

    @Mapping(target = "likesCount", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    @Mapping(target = "commentsCount", expression = "java(post.getComments() != null ? post.getComments().size() : 0)")
    PostCache toPostCache(Post post);

    List<PostCache> toPostCacheList(List<Post> posts);
}