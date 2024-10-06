package faang.school.postservice.mapper.like;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.model.Like;

@Mapper(componentModel = "spring")
public interface LikeMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "comment", ignore = true)
  @Mapping(target = "post", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Like toEntity(LikeRequestDto likeDto);

  @Mapping(source = "post.id", target = "postId")
  @Mapping(source = "comment.id", target = "commentId")
  LikeResponseDto toResponseDto(Like like);

}
