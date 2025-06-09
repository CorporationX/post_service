package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostAndFollowersDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PostAndFollowersMapper {
    PostAndFollowersDto toDto(Post post);
}
