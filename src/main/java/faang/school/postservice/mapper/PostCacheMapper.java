package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CommentCacheMapper.class})
public interface PostCacheMapper {

    @Mapping(source = "likes", target = "likeCount", qualifiedByName = "postCountLikes")
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(source = "comments", target = "comments")
    @Mapping(source = "publishedAt", target = "publishedAt")
    @Mapping(expression = "java(post.getComments() != null ? post.getComments().size() : 0)", target = "commentCount")
    PostCacheDto postToCacheDto(Post post);

    @AfterMapping
    default void fillViewCount(Post post, @MappingTarget PostCacheDto dto) {
        dto.setViewCount(getViewCount(post));
    }

    @Named("postCountLikes")
    default int countLikes(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }

    default int getViewCount(Post post) {
        return (int) (post.getId() % 1000);
    }
}

