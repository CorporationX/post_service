package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeForCommentDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface LikeMapperForComment {
    @Mapping(target = "comment", ignore = true)
    Like toLike(LikeForCommentDto likeForCommentDto);
    LikeForCommentDto toLikeDto(Like like);
    LikeDto toDto(Like like);
}
