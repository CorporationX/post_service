package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toEntity(PostDto dto);

    PostDto toDto(Post post);
}
