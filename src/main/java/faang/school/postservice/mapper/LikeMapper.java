package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.model.Like;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LikeMapper {
    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "postId", source = "post.id")
    LikeDto toDto(Like like);

    @Mapping(target = "comment.id", source = "commentId")
    @Mapping(target = "post.id", source = "postId")
    Like toEntity(LikeDto like);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorPostId", source = "post.authorId")
    LikeEventDto toEvent(Like like);
}
