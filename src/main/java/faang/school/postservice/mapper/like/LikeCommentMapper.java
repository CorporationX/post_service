package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LikeCommentMapper {

    Like toEntity(LikeCommentDto likeCommentDto);
}