package faang.school.postservice.mapper;

import faang.school.postservice.cache.dto.CachedPost;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Hashtag;

import org.mapstruct.Mapper;
import org.mapstruct.Context;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(source = "hashtags", target = "hashtagNames", qualifiedByName = "hashtagToHashtagName")
    PostDto toDto(Post post);

    @Mapping(source = "hashtags", target = "hashtagNames", qualifiedByName = "hashtagToHashtagName")
    @Mapping(target = "countLike", expression = "java(context.getCountLike(post.getId()))")
    PostDto toDto(Post post, @Context PostContextMapper context);

    List<PostDto> toDto(List<Post> posts);

    List<CachedPost> toCachedPost(List<Post> posts);
    CachedPost toCachedPost(Post post);

    @Named("hashtagToHashtagName")
    default String hashtagToHashtagName(Hashtag hashtag) {
        return hashtag.getName();
    }
}
