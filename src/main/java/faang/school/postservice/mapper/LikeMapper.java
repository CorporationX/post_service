package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    LikeDto toLikeDto(Like like);

    List<LikeDto> toLikeDtos(List<Like> likes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Like toLike(LikeDto likeDto);
}
