package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "likeCount", source = "likeCount")
    PostResponseDto toResponseDto(Post post, int likeCount);
}
