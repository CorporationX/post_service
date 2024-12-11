package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.message.event.PostLikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(source = "post.id", target = "postId")
    LikePostDto toLikePostDto(Like like);

    @Mapping(target = "post", ignore = true)
    Like toLike(LikePostDto likePostDto);

    @Mapping(source = "comment.id", target = "commentId")
    LikeCommentDto toLikeCommentDto(Like like);

    @Mapping(target = "comment", ignore = true)
    Like toLike(LikeCommentDto likeDto);
}
