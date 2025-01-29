package faang.school.postservice.mapper;

import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    default Integer getLikesCount(Post post) {
        return post.getLikes() == null ? 0 : post.getLikes().size();
    }

    Post toEntity(PostResultResponse postResultDto);

    @Mapping(expression = "java(getLikesCount(post))", target = "likeCount")
    PostResultResponse toDto(Post post);
}
