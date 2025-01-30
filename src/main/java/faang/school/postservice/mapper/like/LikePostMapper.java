package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LikePostMapper {

    @Mapping(source = "post.id", target = "postId")
    LikePostDto toDto(Like like);

    Like toEntity(LikePostDto likePostDto);
}