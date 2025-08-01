package faang.school.postservice.newsfeed.util.mapping;

import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.newsfeed.dto.PostCacheDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likesAmount", source = "likeIds.size")
    PostCacheDto toPostCacheDto(PostOutputDto postOutputDto);
}
