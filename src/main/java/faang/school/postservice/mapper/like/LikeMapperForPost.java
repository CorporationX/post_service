package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeForPostDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface LikeMapperForPost {
    @Mapping(target = "comment", ignore = true)
    Like toLike(LikeForPostDto likeForPostDto);
    LikeForPostDto toLikeDto(Like like);
    LikeDto toDto(Like like);
}
