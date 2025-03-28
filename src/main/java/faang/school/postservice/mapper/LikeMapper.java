package faang.school.postservice.mapper;

import faang.school.postservice.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    LikeDto toLikeDto(Like like);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "comment", ignore = true)
    Like toLike(LikeDto likeDto);
}
